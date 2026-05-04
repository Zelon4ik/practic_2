package com.mysuperproject.dao;

import com.mysuperproject.entity.Achievement;
import com.mysuperproject.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AchievementDao implements Dao<Achievement, Integer> {

    private static final String FIND_BY_ID =
            "SELECT id, name, requirement_desc FROM achievements WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, name, requirement_desc FROM achievements";
    private static final String SAVE =
            "INSERT INTO achievements (name, requirement_desc) VALUES (?, ?)";
    private static final String UPDATE =
            "UPDATE achievements SET name = ?, requirement_desc = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM achievements WHERE id = ?";

    @Override
    public Optional<Achievement> findById(Integer id) {
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
    public List<Achievement> findAll() {
        List<Achievement> achievements = new ArrayList<>();
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                achievements.add(buildEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return achievements;
    }

    @Override
    public Achievement save(Achievement entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement =
                        connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getRequirementDesc());
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
    public void update(Achievement entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getRequirementDesc());
            statement.setInt(3, entity.getId());
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

    private Achievement buildEntity(ResultSet resultSet) throws SQLException {
        return Achievement.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .requirementDesc(resultSet.getString("requirement_desc"))
                .build();
    }
}
