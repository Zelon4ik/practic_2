-- V6__Add_extra_achievements.sql
-- Додавання додаткових досягнень до бази даних для гейміфікації

-- Для запобігання конфліктів очистимо записи під цими ID
DELETE FROM user_achievements WHERE achievement_id IN (5, 6, 7, 8);
DELETE FROM achievements WHERE id IN (5, 6, 7, 8);

-- Додаємо нові досягнення
INSERT INTO achievements (id, name, requirement_desc) VALUES
(5, 'Марафонець', 'Завершено 5 сесій тренувань'),
(6, 'Непохитний', 'Завершено 10 сесій тренувань'),
(7, 'Робота над помилками', 'Завершено вправу з 3 або більше помилками, але успішним балом (Resilience)'),
(8, 'Справжній Мовознавець', 'Пройдено щонайменше 3 різні вправи з правопису (Broad Knowledge)');
