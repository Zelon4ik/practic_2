package com.mysuperproject.dao;

import com.mysuperproject.entity.Question;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDao extends GenericDao<Question, Integer> {
    public QuestionDao() {
        super(Question.class);
    }

    public List<Question> findByGameId(int gameId) {
        return findAll().stream().filter(q -> q.getGameId() == gameId).collect(Collectors.toList());
    }
}
