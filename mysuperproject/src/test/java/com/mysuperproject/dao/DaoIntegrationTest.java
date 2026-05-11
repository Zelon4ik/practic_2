package com.mysuperproject.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mysuperproject.entity.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DaoIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testUserDao() {
        UserDao userDao = new UserDao();

        // Create
        User user =
                User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .createdAt(LocalDateTime.now())
                        .build();
        User savedUser = userDao.save(user);

        assertThat(savedUser.getId()).isNotNull();

        // Read
        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");

        // Update
        savedUser.setUsername("updateduser");
        userDao.update(savedUser);

        Optional<User> updatedUser = userDao.findById(savedUser.getId());
        assertThat(updatedUser.get().getUsername()).isEqualTo("updateduser");

        // Find All
        List<User> users = userDao.findAll();
        assertThat(users).isNotEmpty();

        // Delete
        userDao.delete(savedUser.getId());
        assertThat(userDao.findById(savedUser.getId())).isEmpty();
    }

    @Test
    void testSubjectDao() {
        SubjectDao subjectDao = new SubjectDao();

        Subject subject = Subject.builder().name("Math").description("Mathematics").build();
        Subject savedSubject = subjectDao.save(subject);
        assertThat(savedSubject.getId()).isNotNull();

        Optional<Subject> foundSubject = subjectDao.findById(savedSubject.getId());
        assertThat(foundSubject).isPresent();
        assertThat(foundSubject.get().getName()).isEqualTo("Math");

        subjectDao.delete(savedSubject.getId());
        assertThat(subjectDao.findById(savedSubject.getId())).isEmpty();
    }

    @Test
    void testGameDao() {
        SubjectDao subjectDao = new SubjectDao();
        Subject subject = subjectDao.save(Subject.builder().name("Science").build());

        GameDao gameDao = new GameDao();
        Game game =
                Game.builder()
                        .subjectId(subject.getId())
                        .title("Physics Quiz")
                        .maxScore(100)
                        .build();
        Game savedGame = gameDao.save(game);
        assertThat(savedGame.getId()).isNotNull();

        Optional<Game> foundGame = gameDao.findById(savedGame.getId());
        assertThat(foundGame).isPresent();
        assertThat(foundGame.get().getTitle()).isEqualTo("Physics Quiz");

        gameDao.delete(savedGame.getId());
        subjectDao.delete(subject.getId());
    }

    @Test
    void testAchievementDao() {
        AchievementDao achievementDao = new AchievementDao();

        Achievement achievement =
                Achievement.builder().name("First Win").requirementDesc("Win a game").build();
        Achievement savedAchievement = achievementDao.save(achievement);
        assertThat(savedAchievement.getId()).isNotNull();

        Optional<Achievement> foundAchievement = achievementDao.findById(savedAchievement.getId());
        assertThat(foundAchievement).isPresent();

        achievementDao.delete(savedAchievement.getId());
    }

    @Test
    void testGameSessionDao() {
        // Setup dependencies
        UserDao userDao = new UserDao();
        User user =
                userDao.save(
                        User.builder().username("sessionuser").email("sess@example.com").build());

        SubjectDao subjectDao = new SubjectDao();
        Subject subject = subjectDao.save(Subject.builder().name("History").build());

        GameDao gameDao = new GameDao();
        Game game =
                gameDao.save(
                        Game.builder()
                                .subjectId(subject.getId())
                                .title("History Quiz")
                                .maxScore(10)
                                .build());

        // Test GameSession
        GameSessionDao sessionDao = new GameSessionDao();
        GameSession session =
                GameSession.builder()
                        .userId(user.getId())
                        .gameId(game.getId())
                        .score(8)
                        .mistakesCount(2)
                        .completedAt(LocalDateTime.now())
                        .build();

        GameSession savedSession = sessionDao.save(session);
        assertThat(savedSession.getId()).isNotNull();

        Optional<GameSession> foundSession = sessionDao.findById(savedSession.getId());
        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().getScore()).isEqualTo(8);

        // Cleanup
        sessionDao.delete(savedSession.getId());
        gameDao.delete(game.getId());
        subjectDao.delete(subject.getId());
        userDao.delete(user.getId());
    }

    @Test
    void testUserAchievements() {
        UserDao userDao = new UserDao();
        User user =
                userDao.save(
                        User.builder()
                                .username("achieveuser")
                                .email("achieve@example.com")
                                .build());

        AchievementDao achievementDao = new AchievementDao();
        Achievement achievement =
                achievementDao.save(
                        Achievement.builder().name("Master").requirementDesc("Mastered").build());

        // Assign achievement
        achievementDao.assignAchievementToUser(user.getId(), achievement.getId());

        // Find achievements for user
        List<Achievement> userAchievements = achievementDao.findAchievementsByUserId(user.getId());
        assertThat(userAchievements).hasSize(1);
        assertThat(userAchievements.get(0).getName()).isEqualTo("Master");

        // Cleanup
        achievementDao.delete(achievement.getId());
        userDao.delete(user.getId());
    }
}
