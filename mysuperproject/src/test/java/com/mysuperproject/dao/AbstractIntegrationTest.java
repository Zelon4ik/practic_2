package com.mysuperproject.dao;

import com.mysuperproject.util.PropertiesUtil;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractIntegrationTest {

    @BeforeAll
    static void beforeAll() {
        String url =
                "jdbc:h2:mem:students_db_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
        String username = "sa";
        String password = "";

        // Run Flyway migrations
        Flyway flyway =
                Flyway.configure()
                        .dataSource(url, username, password)
                        .locations("classpath:db/migration")
                        .cleanDisabled(false)
                        .load();

        flyway.clean();
        flyway.migrate();

        // Overwrite properties so DAOs use H2 DB
        PropertiesUtil.set("db.url", url);
        PropertiesUtil.set("db.username", username);
        PropertiesUtil.set("db.password", password);
    }
}
