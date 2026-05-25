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
import javafx.scene.control.*;
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
    private TableView<GameService.LeaderboardEntry> leaderboardTable;

    private Button reviewBtn;
    private Button certBtn;

    private static boolean isDarkMode = true;

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

        // Theme Toggle Button
        Button themeBtn = new Button("☀️ / 🌙");
        themeBtn.getStyleClass().add("button");
        themeBtn.setOnAction(e -> toggleTheme((Stage) root.getScene().getWindow()));

        Button logoutBtn = new Button("Вийти");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> App.showLoginView());
        header.getChildren().addAll(welcomeLabel, spacer, themeBtn, logoutBtn);

        // Main Content Area
        HBox mainContent = new HBox(30);

        // Left Column: Games and controls
        VBox gamesBox = new VBox(15);
        gamesBox.setPrefWidth(280);
        gamesBox.getStyleClass().add("card");

        Label instructionLabel = new Label("Оберіть вправу:");
        instructionLabel.getStyleClass().add("subtitle-label");
        gamesBox.getChildren().add(instructionLabel);

        java.util.List<com.mysuperproject.entity.Game> allGames = gameService.getAllGames();
        for (com.mysuperproject.entity.Game g : allGames) {
            Button gameButton = new Button(g.getTitle());
            gameButton.getStyleClass().add("button-primary");
            gameButton.setMaxWidth(Double.MAX_VALUE);
            gameButton.setOnAction(e -> startGame(g.getId(), g.getTitle()));
            gamesBox.getChildren().add(gameButton);
        }

        // Mistake Review Button
        reviewBtn = new Button("Робота над помилками");
        reviewBtn.getStyleClass().add("button-success");
        reviewBtn.setMaxWidth(Double.MAX_VALUE);
        reviewBtn.setOnAction(e -> startGame(-1, "Робота над помилками"));
        gamesBox.getChildren().add(reviewBtn);

        // Spelling Rules Reference Book
        Button rulesBtn = new Button("Довідник правил");
        rulesBtn.getStyleClass().add("button");
        rulesBtn.setMaxWidth(Double.MAX_VALUE);
        rulesBtn.setOnAction(e -> showRulesDialog());
        gamesBox.getChildren().add(rulesBtn);

        // Gold-styled Certificate Button
        certBtn = new Button("🎓 Сертифікат грамотності");
        certBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #F59E0B, #D97706); -fx-text-fill: white; -fx-font-weight: bold;");
        certBtn.setMaxWidth(Double.MAX_VALUE);
        certBtn.setOnAction(e -> showCertificate());
        gamesBox.getChildren().add(certBtn);

        // Right Column: Stats and Tables
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
        achDescCol.setCellValueFactory(new PropertyValueFactory<>("requirementDesc"));
        achDescCol.setPrefWidth(250);
        achievementsTable.getColumns().addAll(achNameCol, achDescCol);

        // Sessions Table
        Label sessionsLabel = new Label("Історія тренувань");
        sessionsLabel.getStyleClass().add("subtitle-label");

        sessionsTable = new TableView<>();
        sessionsTable.setPrefHeight(200);

        TableColumn<GameSession, String> gameTitleCol = new TableColumn<>("Вправа");
        gameTitleCol.setCellValueFactory(
                cellData -> {
                    com.mysuperproject.entity.Game g =
                            gameService.getGameById(cellData.getValue().getGameId());
                    return new SimpleStringProperty(g != null ? g.getTitle() : "Невідомо");
                });
        gameTitleCol.setPrefWidth(200);

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
        sessionsTable.getColumns().addAll(gameTitleCol, scoreCol, mistakesCol, dateCol);

        // Class Leaderboard Table
        Label leaderboardLabel = new Label("Рейтинг лідерів класу");
        leaderboardLabel.getStyleClass().add("subtitle-label");

        leaderboardTable = new TableView<>();
        leaderboardTable.setPrefHeight(150);

        TableColumn<GameService.LeaderboardEntry, String> leaderUserCol =
                new TableColumn<>("Студент");
        leaderUserCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        leaderUserCol.setPrefWidth(200);

        TableColumn<GameService.LeaderboardEntry, Integer> leaderScoreCol =
                new TableColumn<>("Сумарний бал");
        leaderScoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        leaderScoreCol.setPrefWidth(150);

        leaderboardTable.getColumns().addAll(leaderUserCol, leaderScoreCol);

        statsBox.getChildren()
                .addAll(
                        achievementsLabel,
                        achievementsTable,
                        sessionsLabel,
                        sessionsTable,
                        leaderboardLabel,
                        leaderboardTable);

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
        leaderboardTable.setItems(
                FXCollections.observableArrayList(gameService.getClassLeaderboard()));

        // Update Mistake Review button text and state
        int mistakesSize = gameService.getUserMistakeQuestions(user).size();
        reviewBtn.setText("Робота над помилками (" + mistakesSize + ")");
        if (mistakesSize == 0) {
            reviewBtn.setDisable(true);
        } else {
            reviewBtn.setDisable(false);
        }

        // Update Certificate button state
        int achievementsSize = gameService.getUserAchievements(user).size();
        if (achievementsSize < 8) {
            certBtn.setDisable(true);
            certBtn.setTooltip(
                    new Tooltip("Отримайте всі 8 досягнень, щоб розблокувати сертифікат!"));
        } else {
            certBtn.setDisable(false);
            certBtn.setTooltip(
                    new Tooltip(
                            "Ви отримали всі досягнення! Натисніть для перегляду сертифікату."));
        }
    }

    private void toggleTheme(Stage stage) {
        isDarkMode = !isDarkMode;
        applyTheme(stage.getScene());
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String stylePath = isDarkMode ? "/styles.css" : "/styles-light.css";
        scene.getStylesheets().add(getClass().getResource(stylePath).toExternalForm());
    }

    private void showRulesDialog() {
        Stage stage = new Stage();
        stage.setTitle("Довідник правил українського правопису");
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.getStyleClass().add("card");

        Label title = new Label("Довідник правил правопису");
        title.getStyleClass().add("title-label");

        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setPrefHeight(450);

        VBox rulesContent = new VBox(12);
        rulesContent.setPadding(new Insets(10));

        rulesContent
                .getChildren()
                .addAll(
                        createRuleCard(
                                "1. Апостроф",
                                "• Пишеться після б, п, в, м, ф перед я, ю, є, ї: б'ю, п'ять, в'язати, м'яч, торф'яний.\n"
                                        + "• Пишеться після р перед я, ю, є, ї (якщо [р] твердий): бур'ян, пір'я, подвір'я.\n"
                                        + "• НЕ пишеться перед я, ю, є, ї, коли перед б, п, в, м, ф є інший приголосний (крім р): мавпячий, духмяний, свято.\n"
                                        + "• В іншомовних словах пишеться після приголосних перед я, ю, є, ї: комп'ютер, бар'єр, п'єдестал (але: бюджет, мюзикл)."),
                        createRuleCard(
                                "2. Подвоєння літер",
                                "• Подвоєння н та нн відбувається на стику морфем (корінь на н + суфікс н): сон -> сонний, день -> денний.\n"
                                        + "• Подовження приголосних д, т, з, с, ц, л, н, ж, ч, ш відбувається в іменниках середнього роду: знання, життя, обличчя, гілля.\n"
                                        + "• НЕ відбувається подвоєння у деяких відмінкових формах іменників жіночого роду: радістю, молодістю."),
                        createRuleCard(
                                "3. М'який знак",
                                "• Пишеться після д, т, з, с, ц, л, н в кінці слова чи складу: день, тінь, донька, тільки.\n"
                                        + "• НЕ пишеться після р в кінці складу (крім Горький): лікар, буквар.\n"
                                        + "• НЕ пишеться після н перед ж, ч, ш, щ та суфіксами -ств-, -ськ-: менший, кінський."),
                        createRuleCard(
                                "4. Спрощення приголосних",
                                "• У групах приголосних -ждн-, -здн-, -стн-, -стл- випадають середні звуки: тиждень -> тижневий, честь -> чесний, область -> обласний.\n"
                                        + "• Винятки (спрощення НЕ відбувається): кістлявий, пестливий, хвастливий, хваснути, випускний."));

        sp.setContent(rulesContent);

        Button closeBtn = new Button("Закрити довідник");
        closeBtn.getStyleClass().add("button-primary");
        closeBtn.setOnAction(e -> stage.close());

        box.getChildren().addAll(title, sp, closeBtn);

        Scene scene = new Scene(box, 550, 600);
        applyTheme(scene);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createRuleCard(String ruleTitle, String ruleBody) {
        VBox card = new VBox(8);
        if (isDarkMode) {
            card.setStyle(
                    "-fx-background-color: #1E293B; -fx-padding: 15px; -fx-background-radius: 8px; -fx-border-color: #334155; -fx-border-radius: 8px;");
        } else {
            card.setStyle(
                    "-fx-background-color: #F1F5F9; -fx-padding: 15px; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");
        }

        Label title = new Label(ruleTitle);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6366F1;");

        Label body = new Label(ruleBody);
        body.setWrapText(true);
        if (isDarkMode) {
            body.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 13px; -fx-line-spacing: 1.4;");
        } else {
            body.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px; -fx-line-spacing: 1.4;");
        }

        card.getChildren().addAll(title, body);
        return card;
    }

    private void showCertificate() {
        Stage stage = new Stage();
        stage.setTitle("Сертифікат Грамотності");
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: #0F172A; "
                        + "-fx-border-color: #F59E0B; "
                        + "-fx-border-width: 6px; "
                        + "-fx-border-radius: 15px; "
                        + "-fx-background-radius: 15px; "
                        + "-fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.4), 20, 0, 0, 0);");

        Label badge = new Label("🎓");
        badge.setStyle("-fx-font-size: 60px;");

        Label certTitle = new Label("СЕРТИФІКАТ ГРАМОТНОСТІ");
        certTitle.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #F59E0B; -fx-font-family: 'Times New Roman'; -fx-letter-spacing: 2px;");

        Label certSubtitle = new Label("Цей сертифікат засвідчує, що");
        certSubtitle.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: #94A3B8; -fx-font-style: italic;");

        Label studentName = new Label(user.getUsername().toUpperCase());
        studentName.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-border-color: transparent transparent #334155 transparent; -fx-border-width: 0 0 2px 0; -fx-padding: 0 0 10 0; -fx-font-family: 'Times New Roman';");

        Label certText =
                new Label(
                        "успішно пройшов повний курс практичних вправ та тренувань у системі 'Smart-тренажер правопису', здобув усі 8 унікальних мовних досягнень та продемонстрував видатні знання з української орфографії.");
        certText.setWrapText(true);
        certText.setAlignment(Pos.CENTER);
        certText.setStyle(
                "-fx-text-fill: #E2E8F0; -fx-font-size: 13px; -fx-line-spacing: 1.5; -fx-alignment: center; -fx-max-width: 400px;");

        HBox sigBox = new HBox(50);
        sigBox.setAlignment(Pos.CENTER);
        sigBox.setPadding(new Insets(20, 0, 10, 0));

        VBox sig1 = new VBox(4);
        sig1.setAlignment(Pos.CENTER);
        Label sigLine1 = new Label("Smart-Тренажер");
        sigLine1.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label sigTitle1 = new Label("Автоматична верифікація");
        sigTitle1.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px;");
        sig1.getChildren().addAll(sigLine1, sigTitle1);

        VBox sig2 = new VBox(4);
        sig2.setAlignment(Pos.CENTER);
        Label sigLine2 = new Label("Nazarii Povkhanych");
        sigLine2.setStyle(
                "-fx-text-fill: #F59E0B; -fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Monotype Corsiva', 'Georgia', serif;");
        Label sigTitle2 = new Label("Розробник системи");
        sigTitle2.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px;");
        sig2.getChildren().addAll(sigLine2, sigTitle2);

        sigBox.getChildren().addAll(sig1, sig2);

        Button closeBtn = new Button("Закрити та зберегти");
        closeBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #F59E0B, #D97706); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10 20;");
        closeBtn.setOnAction(e -> stage.close());

        card.getChildren()
                .addAll(badge, certTitle, certSubtitle, studentName, certText, sigBox, closeBtn);

        Scene scene = new Scene(card, 500, 550);
        stage.setScene(scene);
        stage.show();
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
        applyTheme(scene);
        stage.setScene(scene);
        stage.showAndWait();

        // Refresh data when returning from game
        refreshData();
    }
}
