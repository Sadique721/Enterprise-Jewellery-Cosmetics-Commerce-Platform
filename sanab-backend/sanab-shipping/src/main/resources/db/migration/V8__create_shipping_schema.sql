-- ═══════════════════════════════════════════════════════════════════════════
-- V8 — Shipping Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS shipping;

-- ─────────────────────────────────────────────────────────────────────────────
-- SHIPMENTS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE shipping.shipments (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    order_id                UUID            NOT NULL,
    carrier_name            VARCHAR(100)    NOT NULL,
    tracking_number         VARCHAR(100)    NOT NULL,
    status                  VARCHAR(30)     NOT NULL DEFAULT 'LABEL_CREATED',
    tracking_url            VARCHAR(500),
    estimated_delivery_at   TIMESTAMPTZ,
    delivered_at            TIMESTAMPTZ,

    -- Auditing
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    is_deleted              BOOLEAN         NOT NULL DEFAULT false,
    version                 BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_shipments PRIMARY KEY (id)
);

CREATE INDEX idx_shipments_order_id ON shipping.shipments(order_id);
CREATE INDEX idx_shipments_tracking ON shipping.shipments(tracking_number);
CREATE INDEX idx_shipments_status ON shipping.shipments(status);
