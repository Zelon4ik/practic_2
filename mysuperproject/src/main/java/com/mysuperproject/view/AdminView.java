package com.mysuperproject.view;

import com.mysuperproject.App;
import com.mysuperproject.entity.Subject;
import com.mysuperproject.entity.User;
import com.mysuperproject.viewmodel.SubjectViewModel;
import com.mysuperproject.viewmodel.UserViewModel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class AdminView {
    private final VBox root;

    private final SubjectViewModel subjectViewModel = new SubjectViewModel();
    private final UserViewModel userViewModel = new UserViewModel();
    private final com.mysuperproject.viewmodel.QuestionViewModel questionViewModel =
            new com.mysuperproject.viewmodel.QuestionViewModel();

    public AdminView() {
        root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        // Header
        HBox header = new HBox(10);
        Label title = new Label("Admin Panel - System Data");
        title.getStyleClass().add("title-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-danger");
        logoutBtn.setOnAction(e -> App.showLoginView());
        header.getChildren().addAll(title, spacer, logoutBtn);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Вкладка для Subjects
        Tab subjectTab = new Tab("Subjects", createSubjectView());

        // Вкладка для Users
        Tab userTab = new Tab("Users", createUserView());

        // Вкладка для Questions
        Tab questionTab = new Tab("Questions", createQuestionView());

        tabPane.getTabs().addAll(subjectTab, userTab, questionTab);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().addAll(header, tabPane);
    }

    public VBox getView() {
        return root;
    }

    private VBox createSubjectView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Search bar
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Пошук предмету (фільтрація)...");
        searchField.textProperty().bindBidirectional(subjectViewModel.searchQueryProperty());
        searchBox.getChildren().addAll(new Label("Пошук:"), searchField);

        // Table
        TableView<Subject> table = new TableView<>();

        TableColumn<Subject, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Subject, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Subject, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(idCol, nameCol, descCol);

        // Bind table items to filtered list
        table.setItems(subjectViewModel.getFilteredSubjects());

        // CRUD Form
        HBox formBox = new HBox(10);
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField descField = new TextField();
        descField.setPromptText("Description");

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("button-success");
        addButton.setOnAction(
                e -> {
                    subjectViewModel.addSubject(nameField.getText(), descField.getText());
                    nameField.clear();
                    descField.clear();
                });

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(
                e -> {
                    Subject selected = table.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        subjectViewModel.deleteSubject(selected);
                    }
                });

        formBox.getChildren().addAll(nameField, descField, addButton, deleteButton);

        VBox.setVgrow(table, Priority.ALWAYS);
        vbox.getChildren().addAll(searchBox, table, formBox);
        return vbox;
    }

    private VBox createUserView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Table
        TableView<User> table = new TableView<>();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameCol = new TableColumn<>("Username");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(idCol, nameCol, emailCol);
        table.setItems(userViewModel.getUsers());

        // CRUD Form
        HBox formBox = new HBox(10);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button addButton = new Button("Add User (Register)");
        addButton.getStyleClass().add("button-success");
        addButton.setOnAction(
                e -> {
                    userViewModel.confirmAndAddUser(
                            usernameField.getText(), emailField.getText(), passField.getText());
                    usernameField.clear();
                    emailField.clear();
                    passField.clear();
                });

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(
                e -> {
                    User selected = table.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        userViewModel.deleteUser(selected);
                    }
                });

        formBox.getChildren().addAll(usernameField, emailField, passField, addButton, deleteButton);

        VBox.setVgrow(table, Priority.ALWAYS);
        vbox.getChildren().addAll(table, formBox);
        return vbox;
    }

    private VBox createQuestionView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // ComboBox to select game
        HBox topBox = new HBox(10);
        Label lbl = new Label("Оберіть гру:");
        ComboBox<com.mysuperproject.entity.Game> gameCombo = new ComboBox<>();
        gameCombo.setItems(questionViewModel.getGames());
        // set converter to show game title
        gameCombo.setConverter(
                new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(com.mysuperproject.entity.Game object) {
                        return object == null ? "" : object.getTitle();
                    }

                    @Override
                    public com.mysuperproject.entity.Game fromString(String string) {
                        return null;
                    }
                });
        topBox.getChildren().addAll(lbl, gameCombo);

        TableView<com.mysuperproject.entity.Question> table = new TableView<>();
        TableColumn<com.mysuperproject.entity.Question, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<com.mysuperproject.entity.Question, String> textCol =
                new TableColumn<>("Питання");
        textCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        textCol.setPrefWidth(400);
        TableColumn<com.mysuperproject.entity.Question, String> ansCol =
                new TableColumn<>("Відповідь");
        ansCol.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        ansCol.setPrefWidth(200);
        table.getColumns().addAll(idCol, textCol, ansCol);

        table.setItems(questionViewModel.getQuestions());

        gameCombo
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, oldVal, newVal) -> {
                            if (newVal != null) {
                                questionViewModel.loadQuestionsForGame(newVal.getId());
                            }
                        });

        HBox formBox = new HBox(10);
        TextField qField = new TextField();
        qField.setPromptText("Питання");
        TextField aField = new TextField();
        aField.setPromptText("Відповідь");

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("button-success");
        addBtn.setOnAction(
                e -> {
                    com.mysuperproject.entity.Game selectedGame =
                            gameCombo.getSelectionModel().getSelectedItem();
                    if (selectedGame != null
                            && !qField.getText().isEmpty()
                            && !aField.getText().isEmpty()) {
                        questionViewModel.addQuestion(
                                selectedGame.getId(), qField.getText(), aField.getText());
                        qField.clear();
                        aField.clear();
                    }
                });

        Button delBtn = new Button("Delete");
        delBtn.getStyleClass().add("button-danger");
        delBtn.setOnAction(
                e -> {
                    com.mysuperproject.entity.Question selected =
                            table.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        questionViewModel.deleteQuestion(selected);
                    }
                });

        formBox.getChildren().addAll(qField, aField, addBtn, delBtn);

        VBox.setVgrow(table, Priority.ALWAYS);
        vbox.getChildren().addAll(topBox, table, formBox);
        return vbox;
    }
}
