package com.mysuperproject;

import com.mysuperproject.dao.UserDao;
import com.mysuperproject.entity.User;
import com.mysuperproject.util.ConnectionPool;
import com.mysuperproject.util.PropertiesUtil;
import java.util.List;
import org.flywaydb.core.Flyway;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting mysuperproject...");

        // 1. Run Database Migrations (Flyway)
        System.out.println("Running Flyway migrations...");
        Flyway flyway =
                Flyway.configure()
                        .dataSource(
                                PropertiesUtil.get("db.url"),
                                PropertiesUtil.get("db.username"),
                                PropertiesUtil.get("db.password"))
                        .load();
        flyway.migrate();
        System.out.println("Migrations applied successfully!");

        // 2. Test Connection Pool and DAO
        System.out.println("\nTesting UserDao...");
        UserDao userDao = new UserDao();
        List<User> users = userDao.findAll();

        System.out.println("Users in database: " + users.size());
        for (User user : users) {
            System.out.println(user);
        }

        // 3. Close the connection pool
        ConnectionPool.closePool();
        System.out.println("\nConnection pool closed.");
    }
}
