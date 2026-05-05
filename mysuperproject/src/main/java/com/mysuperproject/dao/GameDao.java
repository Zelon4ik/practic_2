package com.mysuperproject.dao;

import com.mysuperproject.entity.Game;

public class GameDao extends GenericDao<Game, Integer> {
    public GameDao() {
        super(Game.class);
    }
}
