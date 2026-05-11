package com.mysuperproject.viewmodel;

import com.mysuperproject.entity.Game;
import com.mysuperproject.entity.Question;
import com.mysuperproject.service.GameService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QuestionViewModel {
    private final GameService gameService = new GameService();
    private final ObservableList<Question> questions = FXCollections.observableArrayList();
    private final ObservableList<Game> games = FXCollections.observableArrayList();

    public QuestionViewModel() {
        refreshGames();
    }

    public void refreshGames() {
        games.setAll(gameService.getAllGames());
    }

    public void loadQuestionsForGame(int gameId) {
        questions.setAll(gameService.getQuestionsForGame(gameId));
    }

    public void addQuestion(int gameId, String text, String answer) {
        gameService.addQuestion(gameId, text, answer);
        loadQuestionsForGame(gameId);
    }

    public void deleteQuestion(Question q) {
        gameService.deleteQuestion(q.getId());
        loadQuestionsForGame(q.getGameId());
    }

    public ObservableList<Question> getQuestions() {
        return questions;
    }

    public ObservableList<Game> getGames() {
        return games;
    }
}
