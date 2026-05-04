-- V1__Init_schema.sql
-- Створення структури бази даних для навчального застосунку (PostgreSQL)

-- 1. Таблиця Користувачів (Студентів)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Таблиця Предметів/Тем курсу
CREATE TABLE subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT
);

-- 3. Таблиця Навчальних Ігор/Активностей
CREATE TABLE games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    max_score INT NOT NULL,
    CONSTRAINT fk_subject
      FOREIGN KEY (subject_id) 
      REFERENCES subjects(id) 
      ON DELETE CASCADE
);

-- 4. Таблиця Ігрових Сесій (Статистика успішності)
CREATE TABLE game_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
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

-- 5. Таблиця Досягнень
CREATE TABLE achievements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    requirement_desc VARCHAR(255) NOT NULL
);

-- 6. Таблиця Здобутих досягнень користувачів
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
