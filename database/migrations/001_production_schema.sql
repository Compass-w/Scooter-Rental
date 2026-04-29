-- Production schema for ScooterGo.
-- This file creates tables only. Demo users, scooters, bookings, and cards live
-- in database/seeds/dev_seed.sql so production deployments can start clean.

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    city VARCHAR(100),
    avatar_url TEXT,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'customer',
    total_riding_minutes INT DEFAULT 0,
    achievements TEXT DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS scooters (
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

CREATE TABLE IF NOT EXISTS bookings (
    booking_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    scooter_id INT REFERENCES scooters(scooter_id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    planned_end_time TIMESTAMP,
    total_cost DECIMAL(10, 2),
    duration_minutes INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    plan_type VARCHAR(24),
    payment_status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    unlock_status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    unlock_reference VARCHAR(64),
    overtime_fee_per_15_minutes DECIMAL(10, 2) NOT NULL DEFAULT 0,
    overtime_charge_total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    damage_charge_total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    electricity_charge_total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    market_code VARCHAR(8) NOT NULL DEFAULT 'CN',
    service_mode VARCHAR(24) NOT NULL DEFAULT 'SHARING',
    booking_channel VARCHAR(32),
    pickup_store_code VARCHAR(64),
    pickup_store_name VARCHAR(120),
    return_store_code VARCHAR(64),
    return_store_name VARCHAR(120),
    start_battery_level INTEGER,
    estimated_return_battery INTEGER,
    return_battery_level INTEGER,
    liability_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    last_reminder_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bank_cards (
    card_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    card_holder_name VARCHAR(100) NOT NULL,
    card_number_masked VARCHAR(20) NOT NULL,
    expiry_date VARCHAR(5) NOT NULL,
    is_default BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS issue_reports (
    issue_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    scooter_id INTEGER NOT NULL REFERENCES scooters(scooter_id),
    booking_id INTEGER REFERENCES bookings(booking_id),
    category VARCHAR(32) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
    workflow_status VARCHAR(24) NOT NULL DEFAULT 'REPORTED',
    assigned_staff VARCHAR(80),
    safety_action TEXT,
    insurance_case_status VARCHAR(40) NOT NULL DEFAULT 'NOT_REQUIRED',
    customer_charge_policy TEXT,
    repair_charge_estimate DECIMAL(10, 2) NOT NULL DEFAULT 0,
    rider_injured BOOLEAN NOT NULL DEFAULT FALSE,
    third_party_involved BOOLEAN NOT NULL DEFAULT FALSE,
    emergency_services_contacted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS maintenance_logs (
    log_id SERIAL PRIMARY KEY,
    scooter_id INTEGER NOT NULL REFERENCES scooters(scooter_id),
    technician_name VARCHAR(80) NOT NULL,
    action_taken VARCHAR(120) NOT NULL,
    notes TEXT,
    battery_level INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS staff_bookings (
    booking_id SERIAL PRIMARY KEY,
    guest_name VARCHAR(120) NOT NULL,
    guest_email VARCHAR(160),
    scooter_id INTEGER NOT NULL REFERENCES scooters(scooter_id),
    hire_period VARCHAR(24) NOT NULL,
    booking_channel VARCHAR(32) NOT NULL DEFAULT 'WALK_IN_COUNTER',
    pickup_store_code VARCHAR(64),
    pickup_store_name VARCHAR(120),
    return_store_code VARCHAR(64),
    return_store_name VARCHAR(120),
    pickup_battery_level INTEGER,
    expected_return_battery_level INTEGER,
    electricity_delta DECIMAL(10, 2) NOT NULL DEFAULT 0,
    card_holder_name VARCHAR(120),
    card_number_masked VARCHAR(20),
    card_expiry VARCHAR(8),
    payment_status VARCHAR(24) NOT NULL DEFAULT 'CARD_BOUND',
    desired_start_time TIMESTAMP NOT NULL,
    estimated_cost DECIMAL(10, 2) NOT NULL,
    booking_status VARCHAR(24) NOT NULL DEFAULT 'BOOKED',
    confirmation_sent_at TIMESTAMP,
    notes TEXT,
    created_by VARCHAR(80) NOT NULL DEFAULT 'Front Desk',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ops_assignments (
    assignment_id SERIAL PRIMARY KEY,
    staff_name VARCHAR(120) NOT NULL,
    staff_role VARCHAR(32) NOT NULL,
    zone_label VARCHAR(120) NOT NULL,
    shift_status VARCHAR(24) NOT NULL DEFAULT 'READY',
    tasks_in_queue INTEGER NOT NULL DEFAULT 0,
    assigned_scooters INTEGER NOT NULL DEFAULT 0,
    contact_phone VARCHAR(40),
    notes TEXT,
    last_seen_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicle_unlock_sessions (
    command_id SERIAL PRIMARY KEY,
    booking_id INTEGER NOT NULL REFERENCES bookings(booking_id),
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    scooter_id INTEGER NOT NULL REFERENCES scooters(scooter_id),
    scan_token VARCHAR(120) NOT NULL,
    command_status VARCHAR(24) NOT NULL,
    communication_status VARCHAR(24) NOT NULL,
    telemetry_status VARCHAR(24) NOT NULL,
    lock_state VARCHAR(24) NOT NULL,
    device_message TEXT,
    acknowledged_at TIMESTAMP,
    last_heartbeat_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_transactions (
    transaction_id SERIAL PRIMARY KEY,
    booking_id INTEGER REFERENCES bookings(booking_id),
    user_id INTEGER REFERENCES users(user_id),
    scooter_id INTEGER REFERENCES scooters(scooter_id),
    gateway_provider VARCHAR(64) NOT NULL DEFAULT 'SIMULATED_GATEWAY',
    transaction_type VARCHAR(24) NOT NULL,
    charge_category VARCHAR(32) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'CNY',
    status VARCHAR(24) NOT NULL,
    gateway_reference VARCHAR(64) NOT NULL,
    detail TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS automation_events (
    event_id SERIAL PRIMARY KEY,
    booking_id INTEGER REFERENCES bookings(booking_id),
    issue_id INTEGER REFERENCES issue_reports(issue_id),
    event_type VARCHAR(40) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    detail TEXT,
    due_at TIMESTAMP,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_scooter_id ON bookings(scooter_id);
CREATE INDEX IF NOT EXISTS idx_bank_cards_user_id ON bank_cards(user_id);
CREATE INDEX IF NOT EXISTS idx_issue_reports_user_id ON issue_reports(user_id);
CREATE INDEX IF NOT EXISTS idx_issue_reports_scooter_id ON issue_reports(scooter_id);
