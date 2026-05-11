package com.mysuperproject.viewmodel;

import com.mysuperproject.dao.UserDao;
import com.mysuperproject.entity.User;
import com.mysuperproject.infrastructure.email.SmtpEmailSender;
import com.mysuperproject.infrastructure.security.BcryptPasswordHasher;
import com.mysuperproject.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserViewModel {
    private final UserService userService;
    private final ObservableList<User> users = FXCollections.observableArrayList();

    public UserViewModel() {
        this.userService =
                new UserService(new UserDao(), new BcryptPasswordHasher(), new SmtpEmailSender());
        refresh();
    }

    public void refresh() {
        users.setAll(userService.getAllUsers());
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public String sendVerificationCode(String username, String email) {
        userService.checkUserExists(username);
        return userService.sendVerificationCode(email, username);
    }

    public void confirmAndAddUser(String username, String email, String password) {
        userService.register(username, email, password);
        refresh();
    }

    public void deleteUser(User user) {
        userService.deleteUser(user.getId());
        refresh();
    }

    public java.util.Optional<User> login(String username, String password) {
        return userService.login(username, password);
    }
}
