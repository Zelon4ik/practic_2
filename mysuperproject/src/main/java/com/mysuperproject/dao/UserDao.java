package com.mysuperproject.dao;

import com.mysuperproject.entity.User;
import com.mysuperproject.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao implements Dao<User, Integer> {

    private static final String FIND_BY_ID =
            "SELECT id, username, email, created_at FROM users WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, username, email, created_at FROM users";
    private static final String SAVE = "INSERT INTO users (username, email) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE users SET username = ?, email = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM users WHERE id = ?";

    @Override
    public Optional<User> findById(Integer id) {
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
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(buildEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User save(User entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement =
                        connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getEmail());
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
    public void update(User entity) {
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getEmail());
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

    private User buildEntity(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .username(resultSet.getString("username"))
                .email(resultSet.getString("email"))
                .createdAt(
                        resultSet.getTimestamp("created_at") != null
                                ? resultSet.getTimestamp("created_at").toLocalDateTime()
                                : null)
                .build();
    }
}
