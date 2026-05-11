package com.mysuperproject.view;

import com.mysuperproject.App;
import com.mysuperproject.entity.Achievement;
import com.mysuperproject.entity.GameSession;
import com.mysuperproject.entity.User;
import com.mysuperproject.service.GameService;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StudentDashboardView {

    private final VBox root;
    private final User user;
    private final GameService gameService = new GameService();

    private TableView<Achievement> achievementsTable;
    private TableView<GameSession> sessionsTable;

    public StudentDashboardView(User user) {
        this.user = user;

        root = new VBox(20);
        root.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label welcomeLabel = new Label("Кабінет студента: " + user.getUsername());
        welcomeLabel.getStyleClass().add("title-label");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Вийти");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> App.showLoginView());
        header.getChildren().addAll(welcomeLabel, spacer, logoutBtn);

        // Main Content Area
        HBox mainContent = new HBox(30);

        // Left Column: Games
        VBox gamesBox = new VBox(15);
        gamesBox.setPrefWidth(250);
        gamesBox.getStyleClass().add("card");

        Label instructionLabel = new Label("Оберіть предмет:");
        instructionLabel.getStyleClass().add("subtitle-label");

        Button mathButton = new Button("Математика: Дроби");
        mathButton.getStyleClass().add("button-primary");
        mathButton.setMaxWidth(Double.MAX_VALUE);
        mathButton.setOnAction(e -> startGame(1, "Математика: Дроби"));

        Button historyButton = new Button("Історія України");
        historyButton.getStyleClass().add("button-primary");
        historyButton.setMaxWidth(Double.MAX_VALUE);
        historyButton.setOnAction(e -> startGame(3, "Історія України"));

        gamesBox.getChildren().addAll(instructionLabel, mathButton, historyButton);

        // Right Column: Stats
        VBox statsBox = new VBox(15);
        HBox.setHgrow(statsBox, Priority.ALWAYS);

        // Achievements Table
        Label achievementsLabel = new Label("Мої Досягнення");
        achievementsLabel.getStyleClass().add("subtitle-label");

        achievementsTable = new TableView<>();
        achievementsTable.setPrefHeight(150);
        TableColumn<Achievement, String> achNameCol = new TableColumn<>("Назва");
        achNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        achNameCol.setPrefWidth(150);
        TableColumn<Achievement, String> achDescCol = new TableColumn<>("Опис");
        achDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        achDescCol.setPrefWidth(250);
        achievementsTable.getColumns().addAll(achNameCol, achDescCol);

        // Sessions Table
        Label sessionsLabel = new Label("Історія Ігор");
        sessionsLabel.getStyleClass().add("subtitle-label");

        sessionsTable = new TableView<>();
        sessionsTable.setPrefHeight(200);
        TableColumn<GameSession, Integer> scoreCol = new TableColumn<>("Бали");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        TableColumn<GameSession, Integer> mistakesCol = new TableColumn<>("Помилки");
        mistakesCol.setCellValueFactory(new PropertyValueFactory<>("mistakesCount"));
        TableColumn<GameSession, String> dateCol = new TableColumn<>("Дата");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        dateCol.setCellValueFactory(
                cellData -> {
                    if (cellData.getValue().getCompletedAt() != null) {
                        return new SimpleStringProperty(
                                cellData.getValue().getCompletedAt().format(formatter));
                    }
                    return new SimpleStringProperty("");
                });
        dateCol.setPrefWidth(150);
        sessionsTable.getColumns().addAll(scoreCol, mistakesCol, dateCol);

        statsBox.getChildren()
                .addAll(achievementsLabel, achievementsTable, sessionsLabel, sessionsTable);

        mainContent.getChildren().addAll(gamesBox, statsBox);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        root.getChildren().addAll(header, mainContent);

        // Initial data load
        refreshData();
    }

    private void refreshData() {
        achievementsTable.setItems(
                FXCollections.observableArrayList(gameService.getUserAchievements(user)));
        sessionsTable.setItems(
                FXCollections.observableArrayList(gameService.getUserSessions(user)));
    }

    public VBox getView() {
        return root;
    }

    private void startGame(int gameId, String gameTitle) {
        GamePlayView gamePlayView = new GamePlayView(user, gameId, gameTitle);
        Stage stage = new Stage();
        stage.setTitle("Гра: " + gameTitle);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(gamePlayView.getView(), 600, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();

        // Refresh data when returning from game
        refreshData();
    }
}
