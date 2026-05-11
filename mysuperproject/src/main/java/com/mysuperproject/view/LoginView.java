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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Admin Login");
        dialog.setHeaderText("Введіть пароль адміністратора");
        dialog.setContentText("Пароль:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if ("1111".equals(result.get())) {
                App.showAdminView();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Невірний пароль адміністратора!");
                alert.showAndWait();
            }
        }
    }
}
