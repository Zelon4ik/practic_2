package com.mysuperproject.dao;

import com.mysuperproject.entity.UserMistake;
import com.mysuperproject.util.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMistakeDao extends GenericDao<UserMistake, Integer> {
    public UserMistakeDao() {
        super(UserMistake.class);
    }

    public Optional<UserMistake> findByUserAndQuestion(int userId, int questionId) {
        String sql = "SELECT * FROM user_mistakes WHERE user_id = ? AND question_id = ?";
        try (Connection conn = ConnectionPool.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void incrementOrCreate(int userId, int questionId) {
        Optional<UserMistake> existing = findByUserAndQuestion(userId, questionId);
        if (existing.isPresent()) {
            UserMistake um = existing.get();
            um.setMistakeCount(um.getMistakeCount() + 1);
            update(um);
        } else {
            UserMistake um = new UserMistake();
            um.setUserId(userId);
            um.setQuestionId(questionId);
            um.setMistakeCount(1);
            save(um);
        }
    }

    public void deleteByUserAndQuestion(int userId, int questionId) {
        String sql = "DELETE FROM user_mistakes WHERE user_id = ? AND question_id = ?";
        try (Connection conn = ConnectionPool.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, questionId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UserMistake> findByUserId(int userId) {
        List<UserMistake> result = new ArrayList<>();
        String sql = "SELECT * FROM user_mistakes WHERE user_id = ?";
        try (Connection conn = ConnectionPool.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToEntity(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
