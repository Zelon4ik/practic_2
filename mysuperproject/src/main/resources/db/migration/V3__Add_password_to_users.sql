-- V3__Add_password_to_users.sql
-- Додаємо поле пароля для можливості логіну та реєстрації
ALTER TABLE users ADD COLUMN password VARCHAR(255) DEFAULT '12345';
