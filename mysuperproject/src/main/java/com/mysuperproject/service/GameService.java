package com.mysuperproject.service;

import com.mysuperproject.dao.AchievementDao;
import com.mysuperproject.dao.GameDao;
import com.mysuperproject.dao.GameSessionDao;
import com.mysuperproject.dao.QuestionDao;
import com.mysuperproject.dao.UserMistakeDao;
import com.mysuperproject.entity.Achievement;
import com.mysuperproject.entity.Game;
import com.mysuperproject.entity.GameSession;
import com.mysuperproject.entity.Question;
import com.mysuperproject.entity.User;
import com.mysuperproject.entity.UserMistake;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    private final GameSessionDao gameSessionDao = new GameSessionDao();
    private final AchievementDao achievementDao = new AchievementDao();
    private final GameDao gameDao = new GameDao();
    private final QuestionDao questionDao = new QuestionDao();
    private final UserMistakeDao userMistakeDao = new UserMistakeDao();

    public Game getGameById(int id) {
        return gameDao.findById(id).orElse(null);
    }

    public List<Game> getAllGames() {
        return gameDao.findAll();
    }

    public List<Question> getQuestionsForGame(int gameId) {
        return questionDao.findByGameId(gameId);
    }

    public void addQuestion(int gameId, String text, String answer) {
        Question q = new Question();
        q.setGameId(gameId);
        q.setQuestionText(text);
        q.setCorrectAnswer(answer);
        questionDao.save(q);
    }

    public void deleteQuestion(int questionId) {
        questionDao.delete(questionId);
    }

    public void playGame(User user, Game game, int score, int mistakes) {
        // Зберігаємо результати ігрової сесії
        GameSession session = new GameSession();
        session.setUserId(user.getId());
        session.setGameId(game.getId());
        session.setScore(score);
        session.setMistakesCount(mistakes);
        session.setCompletedAt(LocalDateTime.now());
        gameSessionDao.save(session);

        // Перевіряємо умови для отримання досягнень

        // Досягнення 1: "Перший успіх"
        achievementDao.assignAchievementToUser(user.getId(), 1);

        // Досягнення 2: "Абсолютний грамотій" (максимальний бал, без помилок)
        if (score >= game.getMaxScore() && mistakes == 0) {
            achievementDao.assignAchievementToUser(user.getId(), 2);
        }

        // Досягнення 3: "Майстер Апострофа" (бали >= 90 в темі апостроф, gameId = 1 або 2)
        if ((game.getId() == 1 || game.getId() == 2) && score >= 90) {
            achievementDao.assignAchievementToUser(user.getId(), 3);
        }

        // Досягнення 4: "Експерт подвоєння" (бали >= 90 в подвоєнні літер, gameId = 3)
        if (game.getId() == 3 && score >= 90) {
            achievementDao.assignAchievementToUser(user.getId(), 4);
        }

        List<GameSession> userSessions = getUserSessions(user);
        int totalSessions = userSessions.size();

        // Досягнення 5: "Марафонець" (виконано 5 сесій тренувань)
        if (totalSessions >= 5) {
            achievementDao.assignAchievementToUser(user.getId(), 5);
        }

        // Досягнення 6: "Непохитний" (виконано 10 сесій тренувань)
        if (totalSessions >= 10) {
            achievementDao.assignAchievementToUser(user.getId(), 6);
        }

        // Досягнення 7: "Робота над помилками" (завершено з >= 3 помилками, але успішно)
        if (mistakes >= 3 && score >= 50) {
            achievementDao.assignAchievementToUser(user.getId(), 7);
        }

        // Досягнення 8: "Справжній Мовознавець" (пройдено щонайменше 3 різні вправи)
        long distinctGames = userSessions.stream().map(GameSession::getGameId).distinct().count();
        if (distinctGames >= 3) {
            achievementDao.assignAchievementToUser(user.getId(), 8);
        }
    }

    public List<Achievement> getUserAchievements(User user) {
        return achievementDao.findAchievementsByUserId(user.getId());
    }

    public List<GameSession> getUserSessions(User user) {
        List<GameSession> all = gameSessionDao.findAll();
        // Фільтруємо сесії для конкретного користувача
        return all.stream()
                .filter(s -> s.getUserId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public void recordMistake(int userId, int questionId) {
        userMistakeDao.incrementOrCreate(userId, questionId);
    }

    public void resolveMistake(int userId, int questionId) {
        userMistakeDao.deleteByUserAndQuestion(userId, questionId);
    }

    public List<Question> getUserMistakeQuestions(User user) {
        List<UserMistake> mistakes = userMistakeDao.findByUserId(user.getId());
        return mistakes.stream()
                .map(m -> questionDao.findById(m.getQuestionId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<LeaderboardEntry> getClassLeaderboard() {
        List<LeaderboardEntry> leaderboard = new java.util.ArrayList<>();
        String sql =
                "SELECT u.username, SUM(gs.score) as total_score "
                        + "FROM users u "
                        + "JOIN game_sessions gs ON u.id = gs.user_id "
                        + "GROUP BY u.id, u.username "
                        + "ORDER BY total_score DESC";
        try (java.sql.Connection conn = com.mysuperproject.util.ConnectionPool.get();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                leaderboard.add(
                        new LeaderboardEntry(rs.getString("username"), rs.getInt("total_score")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leaderboard;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LeaderboardEntry {
        private String username;
        private Integer totalScore;
    }
}
