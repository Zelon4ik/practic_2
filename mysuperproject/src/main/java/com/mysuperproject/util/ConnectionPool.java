package com.mysuperproject.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool {

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;

    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;

    static {
        initConnectionPool();
    }

    private ConnectionPool() {}

    private static void initConnectionPool() {
        String poolSizeStr = PropertiesUtil.get(POOL_SIZE_KEY);
        int poolSize = poolSizeStr != null ? Integer.parseInt(poolSizeStr) : DEFAULT_POOL_SIZE;
        pool = new ArrayBlockingQueue<>(poolSize);
        sourceConnections = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            Connection connection = open();
            // Wrap the connection with proxy to prevent actual closing when close() is called
            // Instead, we could return it to the pool.
            // For simplicity in this assignment, we'll just use a Proxy.
            var proxyConnection =
                    (Connection)
                            java.lang.reflect.Proxy.newProxyInstance(
                                    ConnectionPool.class.getClassLoader(),
                                    new Class[] {Connection.class},
                                    (proxy, method, args) -> {
                                        if ("close".equals(method.getName())) {
                                            pool.put((Connection) proxy);
                                            return null;
                                        } else {
                                            return method.invoke(connection, args);
                                        }
                                    });
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error getting connection from pool", e);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException("Error opening connection", e);
        }
    }

    public static void closePool() {
        for (Connection connection : sourceConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error closing pool connections", e);
            }
        }
    }
}
