package com.mysuperproject.service;

import com.mysuperproject.dao.UserDao;
import com.mysuperproject.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserService {
    private final UserDao userDao = new UserDao();

    public User register(String username, String email, String password) {
        if (userDao.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Користувач з таким іменем вже існує!");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setCreatedAt(LocalDateTime.now());

        return userDao.save(newUser);
    }

    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // В реальному проекті тут має бути перевірка хешу пароля (напр. BCrypt)
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
