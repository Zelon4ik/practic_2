package com.mysuperproject.dao;

import com.mysuperproject.entity.Achievement;

public class AchievementDao extends GenericDao<Achievement, Integer> {
    public AchievementDao() {
        super(Achievement.class);
    }

    public void assignAchievementToUser(Integer userId, Integer achievementId) {
        String sql = "INSERT IGNORE INTO user_achievements (user_id, achievement_id) VALUES (?, ?)";
        try (java.sql.Connection connection = com.mysuperproject.util.ConnectionPool.get();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, achievementId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public java.util.List<Achievement> findAchievementsByUserId(Integer userId) {
        java.util.List<Achievement> achievements = new java.util.ArrayList<>();
        String sql =
                "SELECT a.* FROM achievements a JOIN user_achievements ua ON a.id = ua.achievement_id WHERE ua.user_id = ?";
        try (java.sql.Connection connection = com.mysuperproject.util.ConnectionPool.get();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            java.sql.ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                achievements.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return achievements;
    }
}
