INSERT INTO scooters (model, latitude, longitude, battery_level, status, base_price, price_per_min)
SELECT
    seed.model,
    seed.latitude,
    seed.longitude,
    seed.battery_level,
    seed.status,
    seed.base_price,
    seed.price_per_min
FROM (
    VALUES
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
    ('Niu KQi2', 30.657438, 104.064758, 90, 'AVAILABLE', 1.00, 0.20),
    ('Segway E2 Plus', 30.659740, 104.080730, 83, 'AVAILABLE', 1.00, 0.20),
    ('Pure Air', 30.641950, 104.076120, 77, 'AVAILABLE', 1.00, 0.20),
    ('Xiaomi Pro 2', 30.582730, 104.067110, 64, 'AVAILABLE', 1.10, 0.22),
    ('Yadea Elite', 30.572850, 104.066860, 52, 'LOW_BATTERY', 1.10, 0.22),
    ('Ninebot Max G2', 30.731520, 104.151320, 89, 'AVAILABLE', 1.20, 0.24),
    ('Niu KQi3', 30.668930, 104.106230, 79, 'AVAILABLE', 1.00, 0.20),
    ('Pure Air 3', 30.694840, 104.112580, 69, 'AVAILABLE', 1.00, 0.20),
    ('Ninebot Max', 39.908700, 116.397500, 94, 'AVAILABLE', 1.20, 0.24),
    ('Xiaomi 1S', 39.986900, 116.305500, 86, 'AVAILABLE', 1.10, 0.22),
    ('Niu MQi GT', 39.939000, 116.428000, 74, 'AVAILABLE', 1.20, 0.24),
    ('Segway E125', 31.230400, 121.473700, 91, 'AVAILABLE', 1.20, 0.24),
    ('Xiaomi Pro 2', 31.223900, 121.445100, 82, 'AVAILABLE', 1.10, 0.22),
    ('Ninebot F40', 31.199800, 121.436500, 58, 'LOW_BATTERY', 1.00, 0.20),
    ('Pure Advance', 23.129100, 113.264400, 88, 'AVAILABLE', 1.10, 0.22),
    ('Yadea C1S', 23.116800, 113.318000, 80, 'AVAILABLE', 1.10, 0.22),
    ('Niu KQi2', 23.098600, 113.319000, 63, 'AVAILABLE', 1.00, 0.20),
    ('Ninebot Max G2', 22.543100, 114.057900, 96, 'AVAILABLE', 1.20, 0.24),
    ('Xiaomi Pro 2', 22.533300, 113.930400, 84, 'AVAILABLE', 1.10, 0.22),
    ('Segway E110A', 22.540500, 114.021000, 71, 'AVAILABLE', 1.20, 0.24),
    ('Pure Air 3', 30.274100, 120.155100, 85, 'AVAILABLE', 1.10, 0.22),
    ('Niu KQi3', 30.258000, 120.130200, 78, 'AVAILABLE', 1.10, 0.22),
    ('Yadea Elite', 30.253400, 120.210800, 62, 'AVAILABLE', 1.10, 0.22),
    ('Xiaomi 1S', 32.060300, 118.796900, 83, 'AVAILABLE', 1.00, 0.20),
    ('Ninebot F40', 32.041500, 118.767400, 57, 'AVAILABLE', 1.00, 0.20),
    ('Niu MQi GT', 30.592800, 114.305500, 87, 'AVAILABLE', 1.10, 0.22),
    ('Segway E2 Plus', 30.543000, 114.357700, 73, 'AVAILABLE', 1.00, 0.20),
    ('Xiaomi Pro 2', 34.341600, 108.939800, 89, 'AVAILABLE', 1.00, 0.20),
    ('Pure Advance', 34.222200, 108.996000, 66, 'AVAILABLE', 1.10, 0.22),
    ('Ninebot Max', 29.563000, 106.551600, 92, 'AVAILABLE', 1.10, 0.22),
    ('Yadea C1S', 29.533100, 106.504900, 61, 'AVAILABLE', 1.10, 0.22),
    ('Niu KQi2', 31.298900, 120.585300, 88, 'AVAILABLE', 1.00, 0.20),
    ('Segway E125', 31.319900, 120.633000, 76, 'AVAILABLE', 1.20, 0.24),
    ('Xiaomi 1S', 22.319300, 114.169400, 81, 'AVAILABLE', 1.20, 0.24),
    ('Ninebot Max G2', 22.284900, 114.158900, 68, 'AVAILABLE', 1.20, 0.24)
) AS seed(model, latitude, longitude, battery_level, status, base_price, price_per_min)
WHERE NOT EXISTS (
    SELECT 1
    FROM scooters existing
    WHERE existing.model = seed.model
      AND existing.latitude = seed.latitude
      AND existing.longitude = seed.longitude
);
