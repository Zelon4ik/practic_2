package com.mysuperproject.uow;

import com.mysuperproject.util.ConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork implements AutoCloseable {
    private final Connection connection;
    private final List<Runnable> newEntities = new ArrayList<>();
    private final List<Runnable> dirtyEntities = new ArrayList<>();
    private final List<Runnable> deletedEntities = new ArrayList<>();

    public UnitOfWork() throws SQLException {
        this.connection = ConnectionPool.get();
        this.connection.setAutoCommit(false); // Start transaction
    }

    public Connection getConnection() {
        return connection;
    }

    public void registerNew(Runnable insertAction) {
        newEntities.add(insertAction);
    }

    public void registerDirty(Runnable updateAction) {
        dirtyEntities.add(updateAction);
    }

    public void registerDeleted(Runnable deleteAction) {
        deletedEntities.add(deleteAction);
    }

    public void commit() throws SQLException {
        try {
            for (Runnable action : newEntities) {
                action.run();
            }
            for (Runnable action : dirtyEntities) {
                action.run();
            }
            for (Runnable action : deletedEntities) {
                action.run();
            }
            connection.commit();
            clear();
        } catch (Exception e) {
            connection.rollback();
            throw new SQLException("Transaction failed, rolled back", e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
            clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        newEntities.clear();
        dirtyEntities.clear();
        deletedEntities.clear();
    }

    @Override
    public void close() throws Exception {
        try {
            rollback(); // Safe rollback just in case commit wasn't called
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.setAutoCommit(true);
                connection.close(); // Return to pool
            }
        }
    }
}
