-- DDL.sql
-- Створення структури бази даних для Smart-тренажера правопису (MySQL/SQLite)

-- 1. Таблиця Користувачів (Студентів)
-- Класифікація: Стрижнева сутність (Master / Довідник).
-- Нормальна форма: 3НФ. 
-- - Всі атрибути неподільні та атомарні (1НФ).
-- - Первинний ключ id, усі неключові атрибути (username, email, password) цілком залежать від нього (2НФ).
-- - Немає транзитивних залежностей (3НФ).
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблиця Розділів Правопису (Тем курсу)
-- Класифікація: Стрижнева сутність / Довідник.
-- Нормальна форма: 3НФ. 
-- - Атрибути скалярні (1НФ), залежать виключно від id (2НФ, 3НФ).
CREATE TABLE subjects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT
);

-- 3. Таблиця Практичних Вправ / Тренажерів
-- Класифікація: Залежна довідкова таблиця (Detail).
-- Нормальна форма: 3НФ. 
-- - Підпорядковується subjects (розділам правопису) через зв'язок 1:M.
CREATE TABLE games (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    max_score INT NOT NULL,
    CONSTRAINT fk_subject
      FOREIGN KEY (subject_id) 
      REFERENCES subjects(id) 
      ON DELETE CASCADE
);

-- 4. Таблиця Завдань / Слів з пропусками
-- Класифікація: Залежна довідкова таблиця (Detail).
-- Нормальна форма: 3НФ.
-- - Підпорядковується games (вправам) через зв'язок 1:M.
CREATE TABLE questions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    game_id INT NOT NULL,
    question_text VARCHAR(255) NOT NULL,
    correct_answer VARCHAR(100) NOT NULL,
    CONSTRAINT fk_game_questions
      FOREIGN KEY (game_id) 
      REFERENCES games(id) 
      ON DELETE CASCADE
);

-- 5. Таблиця Сесій Тренувань (Статистика успішності)
-- Класифікація: Асоціативна таблиця (Перетин). 
-- Фізично реалізує зв'язок "Багато до Багатьох" (M:M) між Users (студентами) та Games (вправами).
-- Нормальна форма: 3НФ. 
CREATE TABLE game_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INT NOT NULL,
    game_id INT NOT NULL,
    score INT NOT NULL,
    mistakes_count INT DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_session
      FOREIGN KEY (user_id) 
      REFERENCES users(id) 
      ON DELETE CASCADE,
    CONSTRAINT fk_game_session
      FOREIGN KEY (game_id) 
      REFERENCES games(id) 
      ON DELETE CASCADE
);

-- 6. Таблиця Досягнень з правопису
-- Класифікація: Довідкова таблиця (Стрижнева).
-- Нормальна форма: 3НФ. Описує незалежні нагороди за досягнення грамотності.
CREATE TABLE achievements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    requirement_desc VARCHAR(255) NOT NULL
);

-- 7. Таблиця Здобутих досягнень користувачів
-- Класифікація: Асоціативна таблиця (Перетин). 
-- Реалізує зв'язок "Багато до Багатьох" (M:M) між Users та Achievements.
-- Нормальна форма: 3НФ. 
CREATE TABLE user_achievements (
    user_id INT NOT NULL,
    achievement_id INT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, achievement_id),
    CONSTRAINT fk_ua_user
      FOREIGN KEY (user_id) 
      REFERENCES users(id) 
      ON DELETE CASCADE,
    CONSTRAINT fk_ua_achieve
      FOREIGN KEY (achievement_id) 
      REFERENCES achievements(id) 
      ON DELETE CASCADE
);
