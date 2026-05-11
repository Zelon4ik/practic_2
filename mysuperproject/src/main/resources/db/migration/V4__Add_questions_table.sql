-- V4__Add_questions_table.sql

CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    question_text VARCHAR(255) NOT NULL,
    correct_answer VARCHAR(100) NOT NULL,
    CONSTRAINT fk_game_questions
      FOREIGN KEY (game_id) 
      REFERENCES games(id) 
      ON DELETE CASCADE
);

-- Seed initial questions for Math (game_id = 1)
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (1, '1. Скільки буде 1/2 + 1/2?', '1');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (1, '2. Скільки буде 3/4 - 1/4?', '2/4');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (1, '3. Знаменник дробу 5/8 це:', '8');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (1, '4. Чисельник дробу 3/7 це:', '3');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (1, '5. Скільки буде 1/3 + 1/3?', '2/3');

-- Seed initial questions for History (game_id = 3)
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (3, '1. В якому році проголошено незалежність УНР?', '1918');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (3, '2. Хто був першим президентом УНР? (Прізвище)', 'Грушевський');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (3, '3. В якому місті підписали Акт Злуки?', 'Київ');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (3, '4. Рік Акту Злуки УНР та ЗУНР:', '1919');
INSERT INTO questions (game_id, question_text, correct_answer) VALUES (3, '5. Як називався уряд УНР у 1917 році?', 'Генеральний Секретаріат');
