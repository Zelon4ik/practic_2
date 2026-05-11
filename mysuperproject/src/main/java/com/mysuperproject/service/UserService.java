package com.mysuperproject.service;

import com.mysuperproject.dao.UserDao;
import com.mysuperproject.entity.User;
import com.mysuperproject.service.port.EmailSender;
import com.mysuperproject.service.port.PasswordHasher;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDao userDao;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;

    public UserService(UserDao userDao, PasswordHasher passwordHasher, EmailSender emailSender) {
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
    }

    public void checkUserExists(String username) {
        if (userDao.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Користувач з таким іменем вже існує!");
        }
    }

    public String sendVerificationCode(String email, String username) {
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);
        String subject = "Код підтвердження MySuperProject";
        String text =
                String.format(
                        "Привіт, %s!\n\nВаш код підтвердження для реєстрації: %s\n\nНікому не повідомляйте цей код.",
                        username, verificationCode);

        emailSender.sendEmail(email, subject, text);
        return verificationCode;
    }

    public User register(String username, String email, String rawPassword) {
        checkUserExists(username);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);

        // Хешуємо пароль перед збереженням
        String hashedPassword = passwordHasher.hash(rawPassword);
        newUser.setPassword(hashedPassword);
        newUser.setCreatedAt(LocalDateTime.now());

        return userDao.save(newUser);
    }

    public Optional<User> login(String username, String rawPassword) {
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Перевіряємо хеш пароля
            if (passwordHasher.verify(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public java.util.List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void deleteUser(int id) {
        userDao.delete(id);
    }
}
