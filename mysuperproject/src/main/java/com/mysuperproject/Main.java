package com.mysuperproject;

import com.mysuperproject.dao.UserDao;
import com.mysuperproject.entity.Achievement;
import com.mysuperproject.entity.Game;
import com.mysuperproject.entity.GameSession;
import com.mysuperproject.entity.User;
import com.mysuperproject.infrastructure.email.SmtpEmailSender;
import com.mysuperproject.infrastructure.security.BcryptPasswordHasher;
import com.mysuperproject.service.GameService;
import com.mysuperproject.service.UserService;
import com.mysuperproject.service.port.EmailSender;
import com.mysuperproject.service.port.PasswordHasher;
import com.mysuperproject.util.ConnectionPool;
import com.mysuperproject.util.PropertiesUtil;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.flywaydb.core.Flyway;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static final UserDao userDao = new UserDao();
    private static final PasswordHasher passwordHasher = new BcryptPasswordHasher();
    private static final EmailSender emailSender = new SmtpEmailSender();
    private static final UserService userService =
            new UserService(userDao, passwordHasher, emailSender);
    private static final GameService gameService = new GameService();
    private static User currentUser = null;

    public static void main(String[] args) {
        initDatabase();

        System.out.println(
                "Вітаємо у застосунку 'Smart-тренажер правопису з системою моніторингу прогресу'!");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showMainMenu();
            } else {
                showUserMenu();
            }
        }

        ConnectionPool.closePool();
        System.out.println("Дякуємо за використання! До побачення.");
    }

    private static void initDatabase() {
        System.out.println("Ініціалізація бази даних...");
        Flyway flyway =
                Flyway.configure()
                        .dataSource(
                                PropertiesUtil.get("db.url"),
                                PropertiesUtil.get("db.username"),
                                PropertiesUtil.get("db.password"))
                        .load();
        flyway.migrate();
        System.out.println("База даних готова до роботи!\n");
    }

    private static boolean showMainMenu() {
        System.out.println("\n--- Головне Меню ---");
        System.out.println("1. Увійти (Логін)");
        System.out.println("2. Зареєструватися");
        System.out.println("3. Вийти з програми");
        System.out.print("Оберіть опцію: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                login();
                return true;
            case "2":
                register();
                return true;
            case "3":
                return false;
            default:
                System.out.println("Невірна опція. Спробуйте ще.");
                return true;
        }
    }

    private static void login() {
        System.out.print("Введіть ім'я користувача: ");
        String username = scanner.nextLine();
        System.out.print("Введіть пароль: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            System.out.println("Успішний вхід! Вітаємо, " + currentUser.getUsername() + "!");
        } else {
            System.out.println("Помилка: Невірне ім'я користувача або пароль.");
        }
    }

    private static void register() {
        System.out.print("Введіть ім'я користувача: ");
        String username = scanner.nextLine();
        System.out.print("Введіть email: ");
        String email = scanner.nextLine();
        System.out.print("Введіть пароль: ");
        String password = scanner.nextLine();

        try {
            currentUser = userService.register(username, email, password);
            System.out.println("Реєстрація успішна! Вітаємо, " + currentUser.getUsername() + "!");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private static void showUserMenu() {
        System.out.println("\n--- Меню Студента (" + currentUser.getUsername() + ") ---");
        System.out.println("1. Тренування: Апостроф у питомих українських словах");
        System.out.println("2. Тренування: Апостроф у словах іншомовного походження");
        System.out.println("3. Переглянути свою статистику та досягнення");
        System.out.println("4. Вийти з акаунту");
        System.out.print("Оберіть опцію: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                playSpellingGame(1);
                break;
            case "2":
                playSpellingGame(2);
                break;
            case "3":
                showStats();
                break;
            case "4":
                currentUser = null;
                System.out.println("Ви вийшли з акаунту.");
                break;
            default:
                System.out.println("Невірна опція. Спробуйте ще.");
        }
    }

    private static void playSpellingGame(int gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            System.out.println("Вправу не знайдено в базі!");
            return;
        }

        System.out.println("\n--- Вправа: " + game.getTitle() + " ---");
        List<com.mysuperproject.entity.Question> questions =
                gameService.getQuestionsForGame(gameId);
        if (questions.isEmpty()) {
            System.out.println("Помилка: Завдання для цієї вправи відсутні в базі!");
            return;
        }

        int correctCount = 0;
        int totalQuestions = questions.size();

        for (int i = 0; i < totalQuestions; i++) {
            com.mysuperproject.entity.Question q = questions.get(i);
            System.out.println("\nЗавдання " + (i + 1) + " з " + totalQuestions + ":");
            System.out.println(q.getQuestionText());
            System.out.print("Ваша відповідь (слово повністю або пропущений символ): ");
            String answer = scanner.nextLine().trim();

            if (isAnswerCorrect(answer, q.getCorrectAnswer())) {
                System.out.println("Правильно! (+)");
                correctCount++;
            } else {
                String displayCorrect = q.getCorrectAnswer();
                if (displayCorrect.contains("|")) {
                    displayCorrect = displayCorrect.split("\\|")[0];
                }
                System.out.println("Неправильно. Правильна відповідь: " + displayCorrect);
            }
        }

        int score = (int) Math.round((double) correctCount / totalQuestions * game.getMaxScore());
        int mistakes = totalQuestions - correctCount;

        gameService.playGame(currentUser, game, score, mistakes);
        System.out.println("\nРезультати вправи збережено!");
        System.out.printf(
                "Ваш бал: %d / %d. Кількість помилок: %d.%n", score, game.getMaxScore(), mistakes);
    }

    private static boolean isAnswerCorrect(String userAnswer, String expectedAnswer) {
        userAnswer = normalizeAnswer(userAnswer);
        expectedAnswer = normalizeAnswer(expectedAnswer);

        if (expectedAnswer.contains("|")) {
            String[] parts = expectedAnswer.split("\\|");
            for (String part : parts) {
                if (userAnswer.equals(part.trim())) {
                    return true;
                }
            }
            return false;
        }

        return userAnswer.equals(expectedAnswer);
    }

    private static String normalizeAnswer(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase().replace("`", "'").replace("’", "'");
    }

    private static void showStats() {
        System.out.println("\n--- Ваша статистика тренувань ---");
        List<GameSession> sessions = gameService.getUserSessions(currentUser);
        if (sessions.isEmpty()) {
            System.out.println("Ви ще не виконали жодної вправи.");
        } else {
            System.out.println("Сесії тренувань:");
            for (GameSession s : sessions) {
                Game g = gameService.getGameById(s.getGameId());
                String gameTitle = (g != null) ? g.getTitle() : "Невідома вправа";
                System.out.printf(
                        "- Вправа: %s | Бали: %d | Помилки: %d | Дата: %s%n",
                        gameTitle, s.getScore(), s.getMistakesCount(), s.getCompletedAt());
            }
        }

        System.out.println("\n--- Ваші досягнення ---");
        List<Achievement> achievements = gameService.getUserAchievements(currentUser);
        if (achievements.isEmpty()) {
            System.out.println("У вас ще немає досягнень.");
        } else {
            for (Achievement a : achievements) {
                System.out.println("- " + a.getName() + " (" + a.getRequirementDesc() + ")");
            }
        }
    }
}
