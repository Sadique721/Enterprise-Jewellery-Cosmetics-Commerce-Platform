-- ═══════════════════════════════════════════════════════════════════════════
-- V4 — Customer Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS customer;

-- ─────────────────────────────────────────────────────────────────────────────
-- PROFILES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE customer.profiles (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id             UUID        NOT NULL,
    avatar_url          VARCHAR(500),
    date_of_birth       DATE,
    gender              VARCHAR(20),
    preferred_language  VARCHAR(50) DEFAULT 'en',
    preferred_currency  VARCHAR(10) DEFAULT 'INR',

    -- Auditing
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN     NOT NULL DEFAULT false,
    version             BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_customer_profiles PRIMARY KEY (id),
    CONSTRAINT uq_customer_user_id UNIQUE (user_id)
);

CREATE INDEX idx_customer_user_id ON customer.profiles(user_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ADDRESSES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE customer.addresses (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    customer_profile_id UUID        NOT NULL,
    full_name           VARCHAR(100) NOT NULL,
    phone               VARCHAR(20) NOT NULL,
    street_address      VARCHAR(250) NOT NULL,
    apartment_suite     VARCHAR(250),
    city                VARCHAR(100) NOT NULL,
    state_province      VARCHAR(100) NOT NULL,
    postal_code         VARCHAR(20) NOT NULL,
    country             VARCHAR(100) NOT NULL DEFAULT 'India',
    address_type        VARCHAR(20) NOT NULL DEFAULT 'BOTH',
    is_default          BOOLEAN     NOT NULL DEFAULT false,

    -- Auditing
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN     NOT NULL DEFAULT false,
    version             BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_customer_addresses PRIMARY KEY (id),
    CONSTRAINT fk_address_profile FOREIGN KEY (customer_profile_id) REFERENCES customer.profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_address_profile_id ON customer.addresses(customer_profile_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- WISHLIST ITEMS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE customer.wishlist_items (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    customer_profile_id UUID        NOT NULL,
    product_id          UUID        NOT NULL,

    -- Auditing
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN     NOT NULL DEFAULT false,
    version             BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_wishlist_items PRIMARY KEY (id),
    CONSTRAINT uq_wishlist_customer_product UNIQUE (customer_profile_id, product_id),
    CONSTRAINT fk_wishlist_profile FOREIGN KEY (customer_profile_id) REFERENCES customer.profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_wishlist_customer_id ON customer.wishlist_items(customer_profile_id);
