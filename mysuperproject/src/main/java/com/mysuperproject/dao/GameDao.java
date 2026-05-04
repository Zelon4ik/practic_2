package com.mysuperproject.dao;

import com.mysuperproject.entity.Game;
import com.mysuperproject.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameDao implements Dao<Game, Integer> {

    private static final String FIND_BY_ID =
            "SELECT id, subject_id, title, max_score FROM games WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, subject_id, title, max_score FROM games";
    private static final String SAVE =
            "INSERT INTO games (subject_id, title, max_score) VALUES (?, ?, ?)";
    private static final String UPDATE =
            "UPDATE games SET subject_id = ?, title = ?, max_score = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM games WHERE id = ?";

    @Override
    public Optional<Game> findById(Integer id) {
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
    public List<Game> findAll() {
        List<Game> games = new ArrayList<>();
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                games.add(buildEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    @Override
    public Game save(Game entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement =
                        connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, entity.getSubjectId());
            statement.setString(2, entity.getTitle());
            statement.setInt(3, entity.getMaxScore());
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
    public void update(Game entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, entity.getSubjectId());
            statement.setString(2, entity.getTitle());
            statement.setInt(3, entity.getMaxScore());
            statement.setInt(4, entity.getId());
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

    private Game buildEntity(ResultSet resultSet) throws SQLException {
        return Game.builder()
                .id(resultSet.getInt("id"))
                .subjectId(resultSet.getInt("subject_id"))
                .title(resultSet.getString("title"))
                .maxScore(resultSet.getInt("max_score"))
                .build();
    }
}
