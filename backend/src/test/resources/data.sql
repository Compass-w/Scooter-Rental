INSERT INTO users (user_id, username, email, phone, city, avatar_url, password_hash, role, total_riding_minutes, achievements)
VALUES
    (1, 'student1', 'student1@leeds.ac.uk', '+447123456789', 'Leeds', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd.g/w2.H4q', 'customer', 80, 'Eco-Warrior'),
    (2, 'manager1', 'manager1@leeds.ac.uk', '+447123450001', 'Leeds', NULL, '$2y$10$o9w2LkXNfi6dgHUIlrMxNuiiUhnxwRlxJ9NwMTLZWGI9ImY64rD4K', 'manager', 12, '');

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 100;
