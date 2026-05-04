package com.mysuperproject.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    Optional<T> findById(K id);

    List<T> findAll();

    T save(T entity);

    void update(T entity);

    void delete(K id);
}
