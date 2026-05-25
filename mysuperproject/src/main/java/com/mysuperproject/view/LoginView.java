package com.mysuperproject.view;

import com.mysuperproject.App;
import com.mysuperproject.entity.User;
import com.mysuperproject.viewmodel.UserViewModel;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginView {

    private final VBox root;
    private final UserViewModel userViewModel = new UserViewModel();

    public LoginView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label title = new Label("Вхід у систему");
        title.getStyleClass().add("title-label");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Ім'я користувача");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        grid.add(new Label("Логін:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);
        grid.add(passwordField, 1, 1);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button loginBtn = new Button("Увійти");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setOnAction(
                e -> {
                    Optional<User> user =
                            userViewModel.login(usernameField.getText(), passwordField.getText());
                    if (user.isPresent()) {
                        App.showStudentDashboard(user.get());
                    } else {
                        errorLabel.setText("Невірний логін або пароль");
                    }
                });

        Button registerBtn = new Button("Реєстрація");
        registerBtn.getStyleClass().add("button-success");
        registerBtn.setOnAction(e -> showRegisterWindow());

        HBox btnBox = new HBox(10, loginBtn, registerBtn);
        btnBox.setAlignment(Pos.CENTER);

        Button adminBtn = new Button("Увійти як Адмін");
        adminBtn.getStyleClass().add("button-link");
        adminBtn.setOnAction(e -> showAdminLoginWindow());

        root.getChildren().addAll(title, grid, errorLabel, btnBox, adminBtn);
    }

    public VBox getView() {
        return root;
    }

    private void showRegisterWindow() {
        RegisterView registerView = new RegisterView(userViewModel);
        Stage stage = new Stage();
        stage.setTitle("Реєстрація");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(registerView.getView(), 350, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void showAdminLoginWindow() {
        // 1. Згенерувати 6-значний код
        String correctCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Повідомити користувача, що код надсилається
        Alert sendingAlert = new Alert(Alert.AlertType.INFORMATION);
        sendingAlert.setTitle("Вхід в адмін-панель");
        sendingAlert.setHeaderText("Надсилання одноразового коду");
        sendingAlert.setContentText(
                "Надсилаємо код підтвердження на email c.povkhanych.nazarii@student.uzhnu.edu.ua...\nБудь ласка, зачекайте.");

        // Відображаємо діалог без очікування (асинхронно)
        sendingAlert.show();

        // 2. Надіслати email в окремому потоці, щоб не фрізити UI
        javafx.concurrent.Task<Void> mailTask =
                new javafx.concurrent.Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        com.mysuperproject.infrastructure.email.SmtpEmailSender sender =
                                new com.mysuperproject.infrastructure.email.SmtpEmailSender();
                        sender.sendEmail(
                                "c.povkhanych.nazarii@student.uzhnu.edu.ua",
                                "Код входу адміністратора Smart-тренажера",
                                "Привіт!\n\nВаш одноразовий код для входу в адмін-панель Smart-тренажера: "
                                        + correctCode
                                        + "\n\nЯкщо ви не робили цього запиту, проігноруйте цей лист.");
                        return null;
                    }
                };

        mailTask.setOnSucceeded(
                e -> {
                    sendingAlert.close();
                    // 3. Запитати код у користувача
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Вхід в адмін-панель");
                    dialog.setHeaderText("Код підтвердження надіслано!");
                    dialog.setContentText(
                            "Введіть 6-значний код з email c.povkhanych.nazarii@student.uzhnu.edu.ua:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        if (correctCode.equals(result.get().trim())) {
                            App.showAdminView();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Помилка");
                            alert.setHeaderText(null);
                            alert.setContentText("Невірний код підтвердження!");
                            alert.showAndWait();
                        }
                    }
                });

        mailTask.setOnFailed(
                e -> {
                    sendingAlert.close();
                    Throwable ex = mailTask.getException();
                    if (ex != null) ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Помилка надсилання");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Не вдалося надіслати email. Перевірте з'єднання з інтернетом або налаштування SMTP у файлі конфігурації.");
                    alert.showAndWait();
                });

        new Thread(mailTask).start();
    }
}
