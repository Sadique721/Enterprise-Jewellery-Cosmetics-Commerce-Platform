-- ═══════════════════════════════════════════════════════════════════════════
-- V1 — Identity Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

-- Create dedicated schema
CREATE SCHEMA IF NOT EXISTS identity;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ─────────────────────────────────────────────────────────────────────────────
-- PERMISSIONS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE identity.permissions (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(300),
    module          VARCHAR(50) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN     NOT NULL DEFAULT false,
    version         BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_name UNIQUE (name)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- ROLES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE identity.roles (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    name            VARCHAR(50) NOT NULL,
    description     VARCHAR(300),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN     NOT NULL DEFAULT false,
    version         BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- ROLE ↔ PERMISSION (JOIN TABLE)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE identity.role_permissions (
    role_id         UUID NOT NULL,
    permission_id   UUID NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES identity.roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES identity.permissions(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE identity.users (
    id                      UUID        NOT NULL DEFAULT gen_random_uuid(),
    first_name              VARCHAR(100) NOT NULL,
    last_name               VARCHAR(100) NOT NULL,
    email                   VARCHAR(150) NOT NULL,
    phone                   VARCHAR(20),
    password_hash           TEXT        NOT NULL,

    -- Status
    status                  VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified          BOOLEAN     NOT NULL DEFAULT false,
    phone_verified          BOOLEAN     NOT NULL DEFAULT false,

    -- MFA
    mfa_enabled             BOOLEAN     NOT NULL DEFAULT false,
    mfa_method              VARCHAR(20),
    totp_secret             VARCHAR(500),

    -- Security tracking
    failed_login_attempts   INT         NOT NULL DEFAULT 0,
    locked_until            TIMESTAMPTZ,
    last_login_at           TIMESTAMPTZ,
    last_login_ip           VARCHAR(50),
    password_changed_at     TIMESTAMPTZ,

    -- Auditing
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    is_deleted              BOOLEAN     NOT NULL DEFAULT false,
    version                 BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_users     PRIMARY KEY (id),
    CONSTRAINT uq_users_email  UNIQUE (email),
    CONSTRAINT uq_users_phone  UNIQUE (phone),
    CONSTRAINT chk_users_status CHECK (status IN (
        'PENDING_VERIFICATION','ACTIVE','TEMPORARILY_LOCKED',
        'LOCKED','SUSPENDED','DEACTIVATED','DELETED'
    ))
);

CREATE INDEX idx_users_email  ON identity.users (email);
CREATE INDEX idx_users_phone  ON identity.users (phone);
CREATE INDEX idx_users_status ON identity.users (status);
CREATE INDEX idx_users_created_at ON identity.users (created_at);

-- ─────────────────────────────────────────────────────────────────────────────
-- USER ↔ ROLE (JOIN TABLE)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE identity.user_roles (
    user_id     UUID NOT NULL,
    role_id     UUID NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES identity.roles(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- USER SESSIONS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE identity.user_sessions (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id             UUID        NOT NULL,
    refresh_token_hash  VARCHAR(64) NOT NULL,
    family_id           VARCHAR(36) NOT NULL,

    -- Device
    device_id           VARCHAR(200),
    device_name         VARCHAR(150),
    user_agent          VARCHAR(500),
    ip_address          VARCHAR(50),
    location            VARCHAR(150),

    -- Lifecycle
    is_active           BOOLEAN     NOT NULL DEFAULT true,
    mfa_verified        BOOLEAN     NOT NULL DEFAULT false,
    expires_at          TIMESTAMPTZ NOT NULL,
    last_used_at        TIMESTAMPTZ,

    -- Auditing
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN     NOT NULL DEFAULT false,
    version             BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_user_sessions PRIMARY KEY (id),
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE
);

CREATE INDEX idx_sessions_user_id      ON identity.user_sessions (user_id);
CREATE INDEX idx_sessions_token_hash   ON identity.user_sessions (refresh_token_hash);
CREATE INDEX idx_sessions_family_id    ON identity.user_sessions (family_id);
CREATE INDEX idx_sessions_active       ON identity.user_sessions (is_active) WHERE is_active = true;
CREATE INDEX idx_sessions_expires      ON identity.user_sessions (expires_at);
