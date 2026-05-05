package com.mysuperproject.uow;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IdentityMap<K, T> {
    private final Map<K, T> map = new HashMap<>();

    public void add(K id, T entity) {
        if (id != null && entity != null) {
            map.put(id, entity);
        }
    }

    public Optional<T> get(K id) {
        return Optional.ofNullable(map.get(id));
    }

    public void remove(K id) {
        map.remove(id);
    }

    public void clear() {
        map.clear();
    }
}
