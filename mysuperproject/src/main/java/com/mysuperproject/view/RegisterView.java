package com.mysuperproject.view;

import com.mysuperproject.viewmodel.UserViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterView {

    private final VBox root;
    private String generatedCode = null;

    public RegisterView(UserViewModel userViewModel) {
        root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Реєстрація");
        title.getStyleClass().add("title-label");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();

        grid.add(new Label("Логін:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(passwordField, 1, 2);

        // Поля для коду (спочатку сховані)
        Label codeLabel = new Label("Код з пошти:");
        TextField codeField = new TextField();
        codeLabel.setVisible(false);
        codeField.setVisible(false);

        grid.add(codeLabel, 0, 3);
        grid.add(codeField, 1, 3);

        Button actionBtn = new Button("Надіслати код");
        actionBtn.getStyleClass().add("button-primary");

        actionBtn.setOnAction(
                e -> {
                    if (generatedCode == null) {
                        // Етап 1: Надсилання коду
                        try {
                            String username = usernameField.getText();
                            String email = emailField.getText();

                            if (username.isEmpty()
                                    || email.isEmpty()
                                    || passwordField.getText().isEmpty()) {
                                throw new IllegalArgumentException("Заповніть усі поля!");
                            }

                            actionBtn.setDisable(true);
                            actionBtn.setText("Надсилаємо...");

                            generatedCode = userViewModel.sendVerificationCode(username, email);

                            // Показуємо поле для вводу коду
                            codeLabel.setVisible(true);
                            codeField.setVisible(true);

                            usernameField.setDisable(true);
                            emailField.setDisable(true);
                            passwordField.setDisable(true);

                            actionBtn.setText("Підтвердити реєстрацію");
                            actionBtn.setDisable(false);

                            showAlert(
                                    Alert.AlertType.INFORMATION,
                                    "Код надіслано",
                                    "Перевірте вашу пошту та введіть код!");

                        } catch (Exception ex) {
                            actionBtn.setDisable(false);
                            actionBtn.setText("Надіслати код");
                            showAlert(Alert.AlertType.ERROR, "Помилка", ex.getMessage());
                        }
                    } else {
                        // Етап 2: Перевірка коду і збереження
                        if (codeField.getText().trim().equals(generatedCode)) {
                            try {
                                userViewModel.confirmAndAddUser(
                                        usernameField.getText(),
                                        emailField.getText(),
                                        passwordField.getText());

                                showAlert(
                                        Alert.AlertType.INFORMATION,
                                        "Успіх",
                                        "Реєстрація успішна!");
                                ((Stage) root.getScene().getWindow()).close();
                            } catch (Exception ex) {
                                showAlert(
                                        Alert.AlertType.ERROR,
                                        "Помилка збереження",
                                        ex.getMessage());
                            }
                        } else {
                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Помилка",
                                    "Невірний код підтвердження!");
                        }
                    }
                });

        root.getChildren().addAll(title, grid, actionBtn);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public VBox getView() {
        return root;
    }
}
