-- 1. Clean up existing tables (Drop in order due to foreign key constraints)
DROP TABLE IF EXISTS bank_cards;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS scooters;
DROP TABLE IF EXISTS users;

-- 2. Create Users Table [ID: 22]
-- Added fields: phone, total_riding_minutes, achievements
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'customer',
    total_riding_minutes INT DEFAULT 0, -- Track total riding time for stats
    achievements TEXT DEFAULT '',      -- Comma-separated list of medals
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create Scooters Table
CREATE TABLE scooters (
    scooter_id SERIAL PRIMARY KEY,
    model VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 6) NOT NULL,
    longitude DECIMAL(10, 6) NOT NULL,
    battery_level INT DEFAULT 100,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    base_price DECIMAL(10, 2) DEFAULT 1.00,
    price_per_min DECIMAL(10, 2) DEFAULT 0.20,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create Bookings Table [ID: 8, 12]
-- Support for booking history, receipts, and cancellation
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    scooter_id INT REFERENCES scooters(scooter_id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_cost DECIMAL(10, 2),
    duration_minutes INT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ACTIVE, COMPLETED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Create Bank Cards Table [ID: 2, 3]
-- Securely store masked card information
CREATE TABLE bank_cards (
    card_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    card_holder_name VARCHAR(100) NOT NULL,
    card_number_masked VARCHAR(20) NOT NULL, -- Format: "**** 1234"
    expiry_date VARCHAR(5) NOT NULL,         -- Format: "MM/YY"
    is_default BOOLEAN DEFAULT false
);

-- 6. Insert Mock Data for Testing

-- Insert Test User (Password is '123456' hashed via BCrypt)
INSERT INTO users (username, email, phone, password_hash, role, total_riding_minutes, achievements) 
VALUES ('student1', 'student1@leeds.ac.uk', '07123456789', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd.g/w2.H4q', 'customer', 80, 'Eco-Warrior,Newcomer');

-- Insert Test Scooters
INSERT INTO scooters (model, latitude, longitude, battery_level, status, base_price, price_per_min) 
VALUES 
('Xiaomi Pro 2', 53.801277, -1.548567, 95, 'AVAILABLE', 1.00, 0.20),
('Ninebot Max', 53.800755, -1.549077, 88, 'AVAILABLE', 1.00, 0.20),
('Pure Air', 53.802100, -1.550000, 15, 'LOW_BATTERY', 1.00, 0.20),
('Xiaomi 1S', 53.804500, -1.547000, 100, 'IN_USE', 1.00, 0.20);

-- Insert Mock Booking History for User 1 (For Stats Chart & History List) [ID: 8, 22]
INSERT INTO bookings (user_id, scooter_id, start_time, end_time, duration_minutes, total_cost, status)
VALUES 
(1, 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days' + INTERVAL '20 minutes', 20, 5.00, 'COMPLETED'),
(1, 2, NOW() - INTERVAL '1 days', NOW() - INTERVAL '1 days' + INTERVAL '45 minutes', 45, 10.00, 'COMPLETED'),
(1, 1, NOW(), NOW() + INTERVAL '15 minutes', 15, 3.50, 'COMPLETED');

-- Insert Mock Bank Card for User 1 [ID: 2, 3]
INSERT INTO bank_cards (user_id, card_holder_name, card_number_masked, expiry_date, is_default)
VALUES (1, 'LZY', '**** 8888', '12/28', true);
