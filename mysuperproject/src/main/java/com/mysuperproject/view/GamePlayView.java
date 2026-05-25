package com.mysuperproject.view;

import com.mysuperproject.entity.Game;
import com.mysuperproject.entity.User;
import com.mysuperproject.service.GameService;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GamePlayView {

    private final ScrollPane root;
    private final GameService gameService = new GameService();
    private final User user;
    private final int gameId;

    private final List<TextField> answerFields = new ArrayList<>();
    private final List<String> correctAnswers = new ArrayList<>();
    private final List<com.mysuperproject.entity.Question> playedQuestions = new ArrayList<>();

    public GamePlayView(User user, int gameId, String gameTitle) {
        this.user = user;
        this.gameId = gameId;

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(gameTitle);
        titleLabel.getStyleClass().add("title-label");
        content.getChildren().add(titleLabel);

        Label scoreLabel =
                new Label("Бали: 0"); // Just a placeholder, as it's not dynamically updated here
        // during play anyway
        scoreLabel.getStyleClass().add("subtitle-label");
        Label mistakesLabel = new Label("Помилки: 0");
        mistakesLabel.getStyleClass().add("subtitle-label");
        mistakesLabel.setStyle("-fx-text-fill: #E74C3C;");
        content.getChildren().addAll(scoreLabel, mistakesLabel);

        setupQuestions(content);

        Button submitBtn = new Button("Завершити та Перевірити");
        submitBtn.getStyleClass().add("button-primary");
        submitBtn.setOnAction(e -> checkAnswersAndSave());

        content.getChildren().add(submitBtn);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
    }

    public ScrollPane getView() {
        return root;
    }

    private void setupQuestions(VBox content) {
        List<com.mysuperproject.entity.Question> questions;
        if (gameId == -1) {
            questions = gameService.getUserMistakeQuestions(user);
        } else {
            questions = gameService.getQuestionsForGame(gameId);
        }

        if (questions.isEmpty()) {
            Label noQ =
                    new Label(
                            gameId == -1
                                    ? "У вас немає невиправлених помилок! Чудова робота!"
                                    : "Немає завдань для цієї гри.");
            noQ.getStyleClass().add("subtitle-label");
            content.getChildren().add(noQ);
            return;
        }

        for (com.mysuperproject.entity.Question q : questions) {
            playedQuestions.add(q);
            addQuestion(content, q.getQuestionText(), q.getCorrectAnswer());
        }
    }

    private void addQuestion(VBox content, String questionText, String correctAnswer) {
        Label qLabel = new Label(questionText);
        qLabel.getStyleClass().add("subtitle-label");
        TextField aField = new TextField();
        aField.setPromptText("Ваша відповідь...");

        answerFields.add(aField);
        correctAnswers.add(correctAnswer);

        String[] choices = parseChoices(questionText);
        if (choices != null) {
            aField.setVisible(false);
            aField.setManaged(false);

            HBox choicesBox = new HBox(10);
            choicesBox.setPadding(new Insets(5, 0, 5, 0));
            ToggleGroup group = new ToggleGroup();

            for (String choice : choices) {
                choice = choice.trim();
                ToggleButton btn = new ToggleButton(choice);
                btn.setToggleGroup(group);
                btn.getStyleClass().add("toggle-button");

                String finalChoice = choice;
                btn.setOnAction(
                        e -> {
                            if (btn.isSelected()) {
                                aField.setText(finalChoice);
                            } else {
                                aField.setText("");
                            }
                        });
                choicesBox.getChildren().add(btn);
            }
            content.getChildren().addAll(qLabel, choicesBox, aField);
        } else {
            content.getChildren().addAll(qLabel, aField);
        }
    }

    private String[] parseChoices(String questionText) {
        if (questionText == null) return null;
        int openBracket = questionText.lastIndexOf("(");
        int closeBracket = questionText.lastIndexOf(")");
        if (openBracket != -1 && closeBracket != -1 && openBracket < closeBracket) {
            String inner = questionText.substring(openBracket + 1, closeBracket);
            if (inner.contains(" чи ")) {
                return inner.split(" чи ");
            }
        }
        return null;
    }

    private void checkAnswersAndSave() {
        int correctCount = 0;
        int totalQuestions = correctAnswers.size();

        for (int i = 0; i < totalQuestions; i++) {
            String userAnswer = answerFields.get(i).getText();
            String expectedAnswer = correctAnswers.get(i);
            com.mysuperproject.entity.Question q = playedQuestions.get(i);

            if (isAnswerCorrect(userAnswer, expectedAnswer)) {
                correctCount++;
                gameService.resolveMistake(user.getId(), q.getId());
            } else {
                gameService.recordMistake(user.getId(), q.getId());
            }
        }

        if (gameId == -1) {
            String resultText =
                    String.format(
                            "Роботу над помилками завершено!\nПравильно відповідей: %d з %d.\nВиправлено помилок: %d.",
                            correctCount, totalQuestions, correctCount);
            showAlert(Alert.AlertType.INFORMATION, "Результат", resultText);
            ((Stage) root.getScene().getWindow()).close();
            return;
        }

        Game game = gameService.getGameById(gameId);
        if (game == null) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Гру не знайдено в базі даних!");
            return;
        }

        int score = (int) Math.round((double) correctCount / totalQuestions * game.getMaxScore());
        int mistakes = totalQuestions - correctCount;

        try {
            gameService.playGame(user, game, score, mistakes);

            String resultText =
                    String.format(
                            "Ви відповіли правильно на %d з %d питань.\nВаш бал: %d.\nКількість помилок: %d.\nРезультати збережено в базу!",
                            correctCount, totalQuestions, score, mistakes);

            showAlert(Alert.AlertType.INFORMATION, "Результат", resultText);

            // Закриваємо вікно гри
            ((Stage) root.getScene().getWindow()).close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка збереження", e.getMessage());
        }
    }

    private boolean isAnswerCorrect(String userAnswer, String expectedAnswer) {
        userAnswer = normalizeAnswer(userAnswer);
        expectedAnswer = normalizeAnswer(expectedAnswer);

        if (expectedAnswer.contains("|")) {
            String[] parts = expectedAnswer.split("\\|");
            for (String part : parts) {
                if (userAnswer.equals(part.trim())) {
                    return true;
                }
            }
            return false;
        }

        return userAnswer.equals(expectedAnswer);
    }

    private String normalizeAnswer(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase().replace("`", "'").replace("’", "'");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
