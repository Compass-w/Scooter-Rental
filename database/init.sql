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
    city VARCHAR(100),
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
INSERT INTO users (username, email, phone, city, password_hash, role, total_riding_minutes, achievements) 
VALUES ('student1', 'student1@leeds.ac.uk', '07123456789', 'Leeds', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd.g/w2.H4q', 'customer', 80, 'Eco-Warrior,Newcomer');

-- Insert Test Scooters
INSERT INTO scooters (model, latitude, longitude, battery_level, status, base_price, price_per_min) 
VALUES 
('Xiaomi Pro 2', 53.801277, -1.548567, 95, 'AVAILABLE', 1.00, 0.20),
('Ninebot Max', 53.800755, -1.549077, 88, 'AVAILABLE', 1.00, 0.20),
('Pure Air', 53.802100, -1.550000, 15, 'LOW_BATTERY', 1.00, 0.20),
('Xiaomi 1S', 53.804500, -1.547000, 100, 'IN_USE', 1.00, 0.20),

-- Chengdu Pidu District / Southwest Jiaotong University (dense cluster)
('Ninebot Max G2', 30.766420, 103.974570, 93, 'AVAILABLE', 1.00, 0.20),
('Xiaomi Pro 2', 30.767180, 103.976820, 87, 'AVAILABLE', 1.00, 0.20),
('Segway E125', 30.768540, 103.978260, 81, 'AVAILABLE', 1.20, 0.22),
('Pure Air 3', 30.764980, 103.972940, 76, 'AVAILABLE', 1.00, 0.20),
('Yadea Elite', 30.769210, 103.981330, 54, 'AVAILABLE', 1.10, 0.22),
('Niu KQi3', 30.771050, 103.985120, 46, 'AVAILABLE', 1.10, 0.22),
('Xiaomi 1S', 30.762870, 103.970880, 31, 'AVAILABLE', 1.00, 0.20),
('Ninebot F40', 30.765960, 103.989450, 18, 'LOW_BATTERY', 1.00, 0.20),
('Segway E110A', 30.770180, 103.973610, 92, 'AVAILABLE', 1.20, 0.24),
('Pure Advance', 30.767930, 103.983940, 84, 'AVAILABLE', 1.10, 0.22),
('Niu MQi GT', 30.763750, 103.986220, 72, 'IN_USE', 1.20, 0.24),
('Xiaomi Pro 2', 30.772260, 103.979580, 67, 'AVAILABLE', 1.00, 0.20),
('Yadea C1S', 30.768770, 103.971660, 95, 'AVAILABLE', 1.10, 0.22),
('Ninebot Max', 30.766880, 103.987960, 61, 'MAINTENANCE', 1.00, 0.20),

-- Greater Chengdu
('Niu KQi2', 30.657438, 104.064758, 90, 'AVAILABLE', 1.00, 0.20),
('Segway E2 Plus', 30.659740, 104.080730, 83, 'AVAILABLE', 1.00, 0.20),
('Pure Air', 30.641950, 104.076120, 77, 'AVAILABLE', 1.00, 0.20),
('Xiaomi Pro 2', 30.582730, 104.067110, 64, 'AVAILABLE', 1.10, 0.22),
('Yadea Elite', 30.572850, 104.066860, 52, 'LOW_BATTERY', 1.10, 0.22),
('Ninebot Max G2', 30.731520, 104.151320, 89, 'AVAILABLE', 1.20, 0.24),
('Niu KQi3', 30.668930, 104.106230, 79, 'AVAILABLE', 1.00, 0.20),
('Pure Air 3', 30.694840, 104.112580, 69, 'AVAILABLE', 1.00, 0.20),

-- Beijing
('Ninebot Max', 39.908700, 116.397500, 94, 'AVAILABLE', 1.20, 0.24),
('Xiaomi 1S', 39.986900, 116.305500, 86, 'AVAILABLE', 1.10, 0.22),
('Niu MQi GT', 39.939000, 116.428000, 74, 'AVAILABLE', 1.20, 0.24),

-- Shanghai
('Segway E125', 31.230400, 121.473700, 91, 'AVAILABLE', 1.20, 0.24),
('Xiaomi Pro 2', 31.223900, 121.445100, 82, 'AVAILABLE', 1.10, 0.22),
('Ninebot F40', 31.199800, 121.436500, 58, 'LOW_BATTERY', 1.00, 0.20),

-- Guangzhou
('Pure Advance', 23.129100, 113.264400, 88, 'AVAILABLE', 1.10, 0.22),
('Yadea C1S', 23.116800, 113.318000, 80, 'AVAILABLE', 1.10, 0.22),
('Niu KQi2', 23.098600, 113.319000, 63, 'AVAILABLE', 1.00, 0.20),

-- Shenzhen
('Ninebot Max G2', 22.543100, 114.057900, 96, 'AVAILABLE', 1.20, 0.24),
('Xiaomi Pro 2', 22.533300, 113.930400, 84, 'AVAILABLE', 1.10, 0.22),
('Segway E110A', 22.540500, 114.021000, 71, 'AVAILABLE', 1.20, 0.24),

-- Hangzhou
('Pure Air 3', 30.274100, 120.155100, 85, 'AVAILABLE', 1.10, 0.22),
('Niu KQi3', 30.258000, 120.130200, 78, 'AVAILABLE', 1.10, 0.22),
('Yadea Elite', 30.253400, 120.210800, 62, 'AVAILABLE', 1.10, 0.22),

-- Nanjing
('Xiaomi 1S', 32.060300, 118.796900, 83, 'AVAILABLE', 1.00, 0.20),
('Ninebot F40', 32.041500, 118.767400, 57, 'AVAILABLE', 1.00, 0.20),

-- Wuhan
('Niu MQi GT', 30.592800, 114.305500, 87, 'AVAILABLE', 1.10, 0.22),
('Segway E2 Plus', 30.543000, 114.357700, 73, 'AVAILABLE', 1.00, 0.20),

-- Xi'an
('Xiaomi Pro 2', 34.341600, 108.939800, 89, 'AVAILABLE', 1.00, 0.20),
('Pure Advance', 34.222200, 108.996000, 66, 'AVAILABLE', 1.10, 0.22),

-- Chongqing
('Ninebot Max', 29.563000, 106.551600, 92, 'AVAILABLE', 1.10, 0.22),
('Yadea C1S', 29.533100, 106.504900, 61, 'AVAILABLE', 1.10, 0.22),

-- Suzhou
('Niu KQi2', 31.298900, 120.585300, 88, 'AVAILABLE', 1.00, 0.20),
('Segway E125', 31.319900, 120.633000, 76, 'AVAILABLE', 1.20, 0.24),

-- Hong Kong
('Xiaomi 1S', 22.319300, 114.169400, 81, 'AVAILABLE', 1.20, 0.24),
('Ninebot Max G2', 22.284900, 114.158900, 68, 'AVAILABLE', 1.20, 0.24);

-- Insert Mock Booking History for User 1 (For Stats Chart & History List) [ID: 8, 22]
INSERT INTO bookings (user_id, scooter_id, start_time, end_time, duration_minutes, total_cost, status)
VALUES 
(1, 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days' + INTERVAL '20 minutes', 20, 5.00, 'COMPLETED'),
(1, 2, NOW() - INTERVAL '1 days', NOW() - INTERVAL '1 days' + INTERVAL '45 minutes', 45, 10.00, 'COMPLETED'),
(1, 1, NOW(), NOW() + INTERVAL '15 minutes', 15, 3.50, 'COMPLETED');

-- Insert Mock Bank Card for User 1 [ID: 2, 3]
INSERT INTO bank_cards (user_id, card_holder_name, card_number_masked, expiry_date, is_default)
VALUES (1, 'LZY', '**** 8888', '12/28', true);
