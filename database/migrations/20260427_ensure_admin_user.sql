-- Migration: ensure the default manager account exists and reset its password.
-- Safe to run multiple times on PostgreSQL.
--
-- Account:
--   username: manager1
--   password: Tyz114031!

INSERT INTO users (
    username,
    email,
    phone,
    city,
    password_hash,
    role,
    total_riding_minutes,
    achievements
)
VALUES (
    'manager1',
    'manager1@leeds.ac.uk',
    '07123450001',
    'Leeds',
    '$2y$10$o9w2LkXNfi6dgHUIlrMxNuiiUhnxwRlxJ9NwMTLZWGI9ImY64rD4K',
    'manager',
    12,
    ''
)
ON CONFLICT (username) DO UPDATE
SET
    email = COALESCE(users.email, EXCLUDED.email),
    phone = COALESCE(users.phone, EXCLUDED.phone),
    city = COALESCE(users.city, EXCLUDED.city),
    password_hash = EXCLUDED.password_hash,
    role = 'manager';
