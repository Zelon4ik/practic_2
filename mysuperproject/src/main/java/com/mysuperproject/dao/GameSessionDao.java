package com.mysuperproject.dao;

import com.mysuperproject.entity.GameSession;

public class GameSessionDao extends GenericDao<GameSession, Integer> {
    public GameSessionDao() {
        super(GameSession.class);
    }
}
