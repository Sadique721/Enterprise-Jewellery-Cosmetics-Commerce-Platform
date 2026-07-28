-- ═══════════════════════════════════════════════════════════════════════════
-- V6 — Orders Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS orders;

-- ─────────────────────────────────────────────────────────────────────────────
-- ORDERS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE orders.orders (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    order_number            VARCHAR(50)     NOT NULL,
    user_id                 UUID            NOT NULL,
    status                  VARCHAR(30)     NOT NULL DEFAULT 'PENDING_PAYMENT',
    subtotal                NUMERIC(12, 2)  NOT NULL,
    shipping_fee            NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    tax_amount              NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    discount_amount         NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    grand_total             NUMERIC(12, 2)  NOT NULL,
    coupon_code             VARCHAR(50),

    -- Shipping Address Snapshot
    shipping_full_name      VARCHAR(100)    NOT NULL,
    shipping_phone          VARCHAR(20)     NOT NULL,
    shipping_address_line   VARCHAR(500)    NOT NULL,
    shipping_city           VARCHAR(100)    NOT NULL,
    shipping_state          VARCHAR(100)    NOT NULL,
    shipping_postal_code    VARCHAR(20)     NOT NULL,
    shipping_country        VARCHAR(100)    NOT NULL,

    -- Tracking
    carrier_name            VARCHAR(100),
    tracking_number         VARCHAR(100),
    estimated_delivery_at   TIMESTAMPTZ,
    delivered_at            TIMESTAMPTZ,

    -- Auditing
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    is_deleted              BOOLEAN         NOT NULL DEFAULT false,
    version                 BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT uq_orders_number UNIQUE (order_number)
);

CREATE INDEX idx_orders_number ON orders.orders(order_number);
CREATE INDEX idx_orders_user_id ON orders.orders(user_id);
CREATE INDEX idx_orders_status ON orders.orders(status);
CREATE INDEX idx_orders_created ON orders.orders(created_at);

-- ─────────────────────────────────────────────────────────────────────────────
-- ORDER ITEMS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE orders.order_items (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    order_id        UUID            NOT NULL,
    product_id      UUID            NOT NULL,
    variant_id      UUID,
    product_name    VARCHAR(200)    NOT NULL,
    sku             VARCHAR(100)    NOT NULL,
    image_url       VARCHAR(500),
    unit_price      NUMERIC(12, 2)  NOT NULL,
    quantity        INT             NOT NULL,
    item_total      NUMERIC(12, 2)  NOT NULL,

    -- Auditing
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN         NOT NULL DEFAULT false,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders.orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON orders.order_items(order_id);
