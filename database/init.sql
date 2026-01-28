-- 1. 清理旧表
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS scooters;
DROP TABLE IF EXISTS users;

-- 2. 创建 Users 表
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('customer', 'manager')) NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 创建 Scooters 表
CREATE TABLE scooters (
    scooter_id SERIAL PRIMARY KEY,
    lat DECIMAL(9, 6) NOT NULL,
    lng DECIMAL(9, 6) NOT NULL,
    battery_level INTEGER CHECK (battery_level BETWEEN 0 AND 100) DEFAULT 100,
    status VARCHAR(20) CHECK (status IN ('available', 'in_use', 'maintenance', 'unavailable')) DEFAULT 'available',
    cost_per_hour DECIMAL(5, 2) DEFAULT 10.00
);

-- 4. 创建 Bookings 表
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    scooter_id INTEGER REFERENCES scooters(scooter_id),
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    total_cost DECIMAL(10, 2),
    status VARCHAR(20) CHECK (status IN ('active', 'completed', 'cancelled')) DEFAULT 'active'
);

-- 5. 插入测试数据
INSERT INTO users (username, email, password_hash, role) 
VALUES ('admin', 'admin@scooter.com', 'hashed_pw', 'manager');

INSERT INTO users (username, email, password_hash, role) 
VALUES ('test_admin', 'test@scooter.com', 'hashed_pw', 'manager');

INSERT INTO scooters (lat, lng, status) VALUES 
(53.801277, -1.548567, 'available');
