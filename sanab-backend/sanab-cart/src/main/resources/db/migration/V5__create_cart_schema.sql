-- ═══════════════════════════════════════════════════════════════════════════
-- V5 — Cart Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS cart;

-- ─────────────────────────────────────────────────────────────────────────────
-- CARTS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE cart.carts (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    user_id                 UUID,
    guest_session_id        VARCHAR(100),
    subtotal                NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    discount_total          NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    grand_total             NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    applied_coupon_code     VARCHAR(50),

    -- Auditing
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    is_deleted              BOOLEAN         NOT NULL DEFAULT false,
    version                 BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_carts PRIMARY KEY (id)
);

CREATE INDEX idx_carts_user_id ON cart.carts(user_id);
CREATE INDEX idx_carts_guest_id ON cart.carts(guest_session_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- CART ITEMS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE cart.cart_items (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    cart_id         UUID            NOT NULL,
    product_id      UUID            NOT NULL,
    variant_id      UUID,
    product_name    VARCHAR(200)    NOT NULL,
    sku             VARCHAR(100),
    image_url       VARCHAR(500),
    unit_price      NUMERIC(12, 2)  NOT NULL,
    quantity        INT             NOT NULL DEFAULT 1,

    -- Auditing
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN         NOT NULL DEFAULT false,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_cart_items PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart.carts(id) ON DELETE CASCADE
);

CREATE INDEX idx_cart_items_cart_id ON cart.cart_items(cart_id);
