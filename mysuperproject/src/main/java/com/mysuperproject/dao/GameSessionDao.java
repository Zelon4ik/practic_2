package com.mysuperproject.dao;

import com.mysuperproject.entity.GameSession;
import com.mysuperproject.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameSessionDao implements Dao<GameSession, Integer> {

    private static final String FIND_BY_ID =
            "SELECT id, user_id, game_id, score, mistakes_count, completed_at FROM game_sessions WHERE id = ?";
    private static final String FIND_ALL =
            "SELECT id, user_id, game_id, score, mistakes_count, completed_at FROM game_sessions";
    private static final String SAVE =
            "INSERT INTO game_sessions (user_id, game_id, score, mistakes_count) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE game_sessions SET user_id = ?, game_id = ?, score = ?, mistakes_count = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM game_sessions WHERE id = ?";

    @Override
    public Optional<GameSession> findById(Integer id) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<GameSession> findAll() {
        List<GameSession> sessions = new ArrayList<>();
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sessions.add(buildEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sessions;
    }

    @Override
    public GameSession save(GameSession entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement =
                        connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, entity.getUserId());
            statement.setInt(2, entity.getGameId());
            statement.setInt(3, entity.getScore());
            statement.setInt(4, entity.getMistakesCount() != null ? entity.getMistakesCount() : 0);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                entity.setId(resultSet.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(GameSession entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, entity.getUserId());
            statement.setInt(2, entity.getGameId());
            statement.setInt(3, entity.getScore());
            statement.setInt(4, entity.getMistakesCount());
            statement.setInt(5, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private GameSession buildEntity(ResultSet resultSet) throws SQLException {
        return GameSession.builder()
                .id(resultSet.getInt("id"))
                .userId(resultSet.getInt("user_id"))
                .gameId(resultSet.getInt("game_id"))
                .score(resultSet.getInt("score"))
                .mistakesCount(resultSet.getInt("mistakes_count"))
                .completedAt(
                        resultSet.getTimestamp("completed_at") != null
                                ? resultSet.getTimestamp("completed_at").toLocalDateTime()
                                : null)
                .build();
    }
}
