package com.mysuperproject.service;

import com.mysuperproject.dao.AchievementDao;
import com.mysuperproject.dao.GameDao;
import com.mysuperproject.dao.GameSessionDao;
import com.mysuperproject.dao.QuestionDao;
import com.mysuperproject.entity.Achievement;
import com.mysuperproject.entity.Game;
import com.mysuperproject.entity.GameSession;
import com.mysuperproject.entity.Question;
import com.mysuperproject.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    private final GameSessionDao gameSessionDao = new GameSessionDao();
    private final AchievementDao achievementDao = new AchievementDao();
    private final GameDao gameDao = new GameDao();
    private final QuestionDao questionDao = new QuestionDao();

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

        // Досягнення 1: "Перші кроки"
        achievementDao.assignAchievementToUser(user.getId(), 1);

        // Досягнення 2: "Ідеально!" (максимальний бал, без помилок)
        if (score >= game.getMaxScore() && mistakes == 0) {
            achievementDao.assignAchievementToUser(user.getId(), 2);
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
}
