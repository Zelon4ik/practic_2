package com.mysuperproject.dao;

import com.mysuperproject.entity.User;

public class UserDao extends GenericDao<User, Integer> {
    public UserDao() {
        super(User.class);
    }

    public java.util.Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (java.sql.Connection connection = com.mysuperproject.util.ConnectionPool.get();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            java.sql.ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return java.util.Optional.of(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return java.util.Optional.empty();
    }
}
