package com.mysuperproject.dao;

import com.mysuperproject.annotation.Column;
import com.mysuperproject.annotation.Id;
import com.mysuperproject.annotation.Table;
import com.mysuperproject.util.ConnectionPool;
import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericDao<T, K> implements Dao<T, K> {
    protected final Class<T> clazz;
    protected final String tableName;
    protected Field idField;
    protected String idColumnName;

    public GenericDao(Class<T> clazz) {
        this.clazz = clazz;
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("@Table annotation missing on " + clazz.getName());
        }
        this.tableName = tableAnnotation.name();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                this.idField = field;
                this.idColumnName = field.getAnnotation(Id.class).name();
                this.idField.setAccessible(true);
                break;
            }
        }
    }

    @Override
    public Optional<T> findById(K id) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public T save(T entity) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder("VALUES (");
        List<Object> params = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        boolean first = true;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)
                    && field.getAnnotation(Id.class).autoIncrement()) {
                continue; // Skip auto-increment ID
            }
            if (field.isAnnotationPresent(Column.class)) {
                if (!first) {
                    sql.append(", ");
                    values.append(", ");
                }
                sql.append(field.getAnnotation(Column.class).name());
                values.append("?");
                try {
                    params.add(field.get(entity));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                first = false;
            }
        }
        sql.append(") ").append(values).append(")");

        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            statement.executeUpdate();

            if (idField != null && idField.getAnnotation(Id.class).autoIncrement()) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idField.set(entity, generatedKeys.getObject(1, idField.getType()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void update(T entity) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        List<Object> params = new ArrayList<>();
        Object idValue = null;

        Field[] fields = clazz.getDeclaredFields();
        boolean first = true;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(Id.class)) {
                    idValue = field.get(entity);
                    continue;
                }
                if (field.isAnnotationPresent(Column.class)) {
                    if (!first) {
                        sql.append(", ");
                    }
                    sql.append(field.getAnnotation(Column.class).name()).append(" = ?");
                    params.add(field.get(entity));
                    first = false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sql.append(" WHERE ").append(idColumnName).append(" = ?");
        params.add(idValue);

        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(K id) {
        String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (Connection connection = ConnectionPool.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected T mapResultSetToEntity(ResultSet resultSet) throws Exception {
        T entity = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String columnName = null;
            if (field.isAnnotationPresent(Id.class)) {
                columnName = field.getAnnotation(Id.class).name();
            } else if (field.isAnnotationPresent(Column.class)) {
                columnName = field.getAnnotation(Column.class).name();
            }

            if (columnName != null) {
                Object value = resultSet.getObject(columnName);
                if (value != null) {
                    if (field.getType().equals(LocalDateTime.class) && value instanceof Timestamp) {
                        field.set(entity, ((Timestamp) value).toLocalDateTime());
                    } else if (field.getType().equals(Integer.class) && value instanceof Number) {
                        field.set(entity, ((Number) value).intValue());
                    } else {
                        field.set(entity, value);
                    }
                }
            }
        }
        return entity;
    }
}
