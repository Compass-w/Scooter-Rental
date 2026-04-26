CREATE TABLE IF NOT EXISTS issue_reports (
    issue_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    scooter_id INTEGER NOT NULL,
    booking_id INTEGER,
    category VARCHAR(32) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
    workflow_status VARCHAR(24) NOT NULL DEFAULT 'REPORTED',
    assigned_staff VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM';
ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS workflow_status VARCHAR(24) NOT NULL DEFAULT 'REPORTED';
ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS assigned_staff VARCHAR(80);
ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE bookings ADD COLUMN IF NOT EXISTS electricity_charge_total DECIMAL(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS market_code VARCHAR(8) NOT NULL DEFAULT 'CN';
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS service_mode VARCHAR(24) NOT NULL DEFAULT 'SHARING';
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS booking_channel VARCHAR(32);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS pickup_store_code VARCHAR(64);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS pickup_store_name VARCHAR(120);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS return_store_code VARCHAR(64);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS return_store_name VARCHAR(120);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS start_battery_level INTEGER;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS estimated_return_battery INTEGER;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS return_battery_level INTEGER;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS liability_accepted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS maintenance_logs (
    log_id SERIAL PRIMARY KEY,
    scooter_id INTEGER NOT NULL,
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
    scooter_id INTEGER NOT NULL,
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

ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS booking_channel VARCHAR(32) NOT NULL DEFAULT 'WALK_IN_COUNTER';
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS pickup_store_code VARCHAR(64);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS pickup_store_name VARCHAR(120);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS return_store_code VARCHAR(64);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS return_store_name VARCHAR(120);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS pickup_battery_level INTEGER;
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS expected_return_battery_level INTEGER;
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS electricity_delta DECIMAL(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS card_holder_name VARCHAR(120);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS card_number_masked VARCHAR(20);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS card_expiry VARCHAR(8);
ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS payment_status VARCHAR(24) NOT NULL DEFAULT 'CARD_BOUND';
