-- noinspection SqlNoDataSourceInspectionForFile

-- Очищаем таблицу перед вставкой, чтобы тесты были изолированы
-- (Хотя @Transactional и так это делает, это дополнительная гарантия)
TRUNCATE TABLE tasks RESTART IDENTITY;

-- Вставляем тестовые данные
INSERT INTO tasks (title, description, completed, created_at, updated_at) VALUES
('First Task', 'Description for first task', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Second Task', 'Description for second task', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Third Task', 'Description for third task', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);