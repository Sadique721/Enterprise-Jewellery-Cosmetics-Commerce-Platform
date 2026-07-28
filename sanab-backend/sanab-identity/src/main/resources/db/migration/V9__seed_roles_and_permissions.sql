-- ═══════════════════════════════════════════════════════════════════════════
-- V9 — Seed: Default Roles & Permissions
-- SANAB Enterprise Commerce Platform
-- ═══════════════════════════════════════════════════════════════════════════

-- ─── Permissions ─────────────────────────────────────────────────────────────

INSERT INTO identity.permissions (id, name, description, module) VALUES
-- Catalog
(gen_random_uuid(), 'catalog:read',        'View products and categories',          'catalog'),
(gen_random_uuid(), 'catalog:write',       'Create and update products',            'catalog'),
(gen_random_uuid(), 'catalog:delete',      'Delete products and categories',        'catalog'),
-- Orders
(gen_random_uuid(), 'orders:read',         'View all orders',                       'orders'),
(gen_random_uuid(), 'orders:write',        'Create and update orders',              'orders'),
(gen_random_uuid(), 'orders:manage',       'Full order management (cancel/refund)', 'orders'),
-- Customers
(gen_random_uuid(), 'customers:read',      'View customer accounts',                'customers'),
(gen_random_uuid(), 'customers:write',     'Update customer accounts',              'customers'),
(gen_random_uuid(), 'customers:ban',       'Suspend or ban customer accounts',      'customers'),
-- Payments
(gen_random_uuid(), 'payments:read',       'View payment transactions',             'payments'),
(gen_random_uuid(), 'payments:refund',     'Process payment refunds',               'payments'),
-- Inventory
(gen_random_uuid(), 'inventory:read',      'View inventory levels',                 'inventory'),
(gen_random_uuid(), 'inventory:write',     'Update inventory levels',               'inventory'),
-- Promotions
(gen_random_uuid(), 'promotions:read',     'View coupons and promotions',           'promotions'),
(gen_random_uuid(), 'promotions:write',    'Create and manage promotions',          'promotions'),
-- Analytics
(gen_random_uuid(), 'analytics:read',      'View dashboards and reports',           'analytics'),
-- Notifications
(gen_random_uuid(), 'notifications:read',  'View notification history',             'notifications'),
(gen_random_uuid(), 'notifications:write', 'Send and manage notifications',         'notifications'),
-- Admin
(gen_random_uuid(), 'admin:users',         'Full user management',                  'admin'),
(gen_random_uuid(), 'admin:roles',         'Manage roles and permissions',          'admin'),
(gen_random_uuid(), 'admin:settings',      'Manage platform settings',              'admin'),
(gen_random_uuid(), 'admin:audit',         'View audit logs',                       'admin');

-- ─── Roles ───────────────────────────────────────────────────────────────────

INSERT INTO identity.roles (id, name, description) VALUES
('00000000-0000-0000-0000-000000000001', 'SUPER_ADMIN',  'Full platform access — Antigravity team only'),
('00000000-0000-0000-0000-000000000002', 'ADMIN',        'Store administrator with full management access'),
('00000000-0000-0000-0000-000000000003', 'MANAGER',      'Operations manager — orders, inventory, customers'),
('00000000-0000-0000-0000-000000000004', 'SUPPORT',      'Customer support agent — view orders and customers'),
('00000000-0000-0000-0000-000000000005', 'CUSTOMER',     'Registered customer account');

-- ─── Role Permissions: SUPER_ADMIN (all) ─────────────────────────────────────

INSERT INTO identity.role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000001', id FROM identity.permissions;

-- ─── Role Permissions: ADMIN (all except super-admin ops) ────────────────────

INSERT INTO identity.role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000002', id
FROM identity.permissions
WHERE name NOT IN ('admin:roles');

-- ─── Role Permissions: MANAGER ───────────────────────────────────────────────

INSERT INTO identity.role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000003', id
FROM identity.permissions
WHERE name IN (
    'catalog:read','catalog:write',
    'orders:read','orders:write','orders:manage',
    'customers:read',
    'inventory:read','inventory:write',
    'promotions:read','promotions:write',
    'analytics:read',
    'notifications:read'
);

-- ─── Role Permissions: SUPPORT ───────────────────────────────────────────────

INSERT INTO identity.role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000004', id
FROM identity.permissions
WHERE name IN (
    'catalog:read',
    'orders:read',
    'customers:read',
    'notifications:read'
);

-- CUSTOMER role has no admin permissions — access controlled at service level
