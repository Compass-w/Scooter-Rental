-- 1. 清理旧表
DROP TABLE IF EXISTS scooters;
DROP TABLE IF EXISTS users;

-- 2. 创建用户表
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 创建滑板车表
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

-- 4. 插入测试数据 (新库是空的，必须插数据！)
INSERT INTO scooters (model, latitude, longitude, battery_level, status, base_price, price_per_min) 
VALUES 
('Xiaomi Pro 2', 53.801277, -1.548567, 95, 'AVAILABLE', 1.00, 0.20),
('Ninebot Max', 53.800755, -1.549077, 88, 'AVAILABLE', 1.00, 0.20),
('Pure Air', 53.802100, -1.550000, 15, 'LOW_BATTERY', 1.00, 0.20),
('Xiaomi 1S', 53.804500, -1.547000, 100, 'IN_USE', 1.00, 0.20);

INSERT INTO users (username, password_hash, role) 
VALUES ('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd.g/w2.H4q', 'customer');
