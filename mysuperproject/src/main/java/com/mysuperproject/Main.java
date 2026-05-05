package com.mysuperproject;

import com.mysuperproject.entity.Achievement;
import com.mysuperproject.entity.Game;
import com.mysuperproject.entity.GameSession;
import com.mysuperproject.entity.User;
import com.mysuperproject.service.GameService;
import com.mysuperproject.service.UserService;
import com.mysuperproject.util.ConnectionPool;
import com.mysuperproject.util.PropertiesUtil;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.flywaydb.core.Flyway;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final GameService gameService = new GameService();
    private static User currentUser = null;

    public static void main(String[] args) {
        initDatabase();

        System.out.println("Вітаємо у застосунку 'Student Progress Tracking'!");

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
        System.out.println("1. Зіграти в гру 'Математика: Дроби'");
        System.out.println("2. Зіграти в гру 'Історія України'");
        System.out.println("3. Переглянути свою статистику та досягнення");
        System.out.println("4. Вийти з акаунту");
        System.out.print("Оберіть опцію: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                playMathGame();
                break;
            case "2":
                playHistoryGame();
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

    private static void playMathGame() {
        // ID гри 'Знайди спільний знаменник' = 1 (див. V2__Seed_data.sql)
        Game game = gameService.getGameById(1);
        if (game == null) {
            System.out.println("Гру не знайдено в базі!");
            return;
        }

        System.out.println("\nГра: " + game.getTitle());
        System.out.println("Питання: Чому дорівнює 1/2 + 1/2? (Введіть число)");
        System.out.print("Ваша відповідь: ");
        String answer = scanner.nextLine();

        int score = 0;
        int mistakes = 1;
        if ("1".equals(answer.trim())) {
            System.out.println("Правильно!");
            score = game.getMaxScore(); // 100 балів
            mistakes = 0;
        } else {
            System.out.println("Неправильно. Правильна відповідь: 1");
        }

        gameService.playGame(currentUser, game, score, mistakes);
        System.out.println("Результат збережено!");
    }

    private static void playHistoryGame() {
        // ID гри 'Хронологія: УНР' = 3 (див. V2__Seed_data.sql)
        Game game = gameService.getGameById(3);
        if (game == null) {
            System.out.println("Гру не знайдено в базі!");
            return;
        }

        System.out.println("\nГра: " + game.getTitle());
        System.out.println(
                "Питання: В якому році була проголошена незалежність УНР? (Введіть рік)");
        System.out.print("Ваша відповідь: ");
        String answer = scanner.nextLine();

        int score = 0;
        int mistakes = 1;
        if ("1918".equals(answer.trim())) {
            System.out.println("Правильно!");
            score = game.getMaxScore(); // 100 балів
            mistakes = 0;
        } else {
            System.out.println("Неправильно. Правильна відповідь: 1918");
        }

        gameService.playGame(currentUser, game, score, mistakes);
        System.out.println("Результат збережено!");
    }

    private static void showStats() {
        System.out.println("\n--- Ваша статистика ---");
        List<GameSession> sessions = gameService.getUserSessions(currentUser);
        if (sessions.isEmpty()) {
            System.out.println("Ви ще не зіграли жодної гри.");
        } else {
            System.out.println("Ігрові сесії:");
            for (GameSession s : sessions) {
                System.out.printf(
                        "- Гра ID: %d | Бали: %d | Помилки: %d | Дата: %s%n",
                        s.getGameId(), s.getScore(), s.getMistakesCount(), s.getCompletedAt());
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
