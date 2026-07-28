-- ═══════════════════════════════════════════════════════════════════════════
-- V10 — Inventory Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS inventory;

-- ─────────────────────────────────────────────────────────────────────────────
-- ITEMS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE inventory.items (
    id                      UUID        NOT NULL DEFAULT gen_random_uuid(),
    product_id              UUID        NOT NULL,
    variant_id              UUID,
    sku                     VARCHAR(100) NOT NULL,
    available_quantity      INT         NOT NULL DEFAULT 0,
    reserved_quantity       INT         NOT NULL DEFAULT 0,
    low_stock_threshold     INT         NOT NULL DEFAULT 5,
    warehouse_location      VARCHAR(100) DEFAULT 'MAIN_WAREHOUSE',

    -- Auditing
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    is_deleted              BOOLEAN     NOT NULL DEFAULT false,
    version                 BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_inventory_items PRIMARY KEY (id),
    CONSTRAINT uq_inventory_sku UNIQUE (sku)
);

CREATE INDEX idx_inventory_product_id ON inventory.items(product_id);
CREATE INDEX idx_inventory_sku ON inventory.items(sku);

-- ─────────────────────────────────────────────────────────────────────────────
-- RESERVATIONS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE inventory.reservations (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    cart_id         UUID        NOT NULL,
    product_id      UUID        NOT NULL,
    sku             VARCHAR(100) NOT NULL,
    quantity        INT         NOT NULL,
    expires_at      TIMESTAMPTZ NOT NULL,
    is_fulfilled    BOOLEAN     NOT NULL DEFAULT false,

    -- Auditing
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN     NOT NULL DEFAULT false,
    version         BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_inventory_reservations PRIMARY KEY (id)
);

CREATE INDEX idx_reservations_cart_id ON inventory.reservations(cart_id);
CREATE INDEX idx_reservations_sku ON inventory.reservations(sku);
CREATE INDEX idx_reservations_expires ON inventory.reservations(expires_at);
