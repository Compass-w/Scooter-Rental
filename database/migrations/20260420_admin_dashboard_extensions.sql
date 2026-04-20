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
    desired_start_time TIMESTAMP NOT NULL,
    estimated_cost DECIMAL(10, 2) NOT NULL,
    booking_status VARCHAR(24) NOT NULL DEFAULT 'BOOKED',
    confirmation_sent_at TIMESTAMP,
    notes TEXT,
    created_by VARCHAR(80) NOT NULL DEFAULT 'Front Desk',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
