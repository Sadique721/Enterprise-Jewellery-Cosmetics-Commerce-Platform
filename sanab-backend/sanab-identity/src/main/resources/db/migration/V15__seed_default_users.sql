-- ═══════════════════════════════════════════════════════════════════════════
-- V15 — Seed Admin & Customer Accounts
-- SANAB Enterprise Commerce Platform
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO identity.users (
    id, first_name, last_name, email, phone, password_hash, status, email_verified, phone_verified
) VALUES
(
    '00000000-0000-0000-0000-000000000786',
    'Sadique',
    'Amin (Admin)',
    'mdsadiqueamin721786@gmail.com',
    '+919876543210',
    '$2a$10$EblZqNptyYvcLmEwVDCE7.Wq8tYV.C0G6y0r.N8u8Q1Z1vXy4x5mO',
    'ACTIVE',
    true,
    true
),
(
    '00000000-0000-0000-0000-000000000721',
    'Sadique',
    'Amin (Customer)',
    'mdsadiqueamin721721@gmail.com',
    '+919876543211',
    '$2a$10$EblZqNptyYvcLmEwVDCE7.Wq8tYV.C0G6y0r.N8u8Q1Z1vXy4x5mO',
    'ACTIVE',
    true,
    true
)
ON CONFLICT (email) DO UPDATE SET status = 'ACTIVE';

INSERT INTO identity.user_roles (user_id, role_id) VALUES
('00000000-0000-0000-0000-000000000786', '00000000-0000-0000-0000-000000000001'),
('00000000-0000-0000-0000-000000000721', '00000000-0000-0000-0000-000000000005')
ON CONFLICT DO NOTHING;
