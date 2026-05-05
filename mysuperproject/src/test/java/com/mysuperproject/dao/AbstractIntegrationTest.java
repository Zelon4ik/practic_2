package com.mysuperproject.dao;

import com.mysuperproject.util.PropertiesUtil;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractIntegrationTest {

    @BeforeAll
    static void beforeAll() {
        // Підключення до локальної тестової бази даних
        // Якщо база даних не існує, вона буде автоматично створена завдяки параметру
        // createDatabaseIfNotExist=true
        String url =
                "jdbc:mysql://localhost:3306/students_db_test?createDatabaseIfNotExist=true&serverTimezone=UTC";
        // Стандартні дані для локальних серверів MySQL (XAMPP/OpenServer). Якщо у вас інший пароль
        // - змініть тут.
        String username = "root";
        String password = "";

        // Запускаємо міграції Flyway для створення таблиць у тестовій базі
        Flyway flyway =
                Flyway.configure()
                        .dataSource(url, username, password)
                        .locations("classpath:db/migration")
                        .cleanDisabled(false) // Дозволяємо очищати БД перед кожним запуском
                        .load();

        // Очищаємо тестову базу перед тестами, щоб уникнути конфліктів старих даних
        flyway.clean();
        // Застосовуємо міграції
        flyway.migrate();

        // Перевизначаємо налаштування для DAOs, щоб вони працювали з тестовою базою замість
        // основної
        PropertiesUtil.set("db.url", url);
        PropertiesUtil.set("db.username", username);
        PropertiesUtil.set("db.password", password);
    }
}
