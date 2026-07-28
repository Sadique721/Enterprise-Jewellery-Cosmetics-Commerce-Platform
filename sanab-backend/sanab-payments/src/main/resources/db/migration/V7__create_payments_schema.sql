-- ═══════════════════════════════════════════════════════════════════════════
-- V7 — Payments Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS payments;

-- ─────────────────────────────────────────────────────────────────────────────
-- TRANSACTIONS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE payments.transactions (
    id                          UUID            NOT NULL DEFAULT gen_random_uuid(),
    order_id                    UUID            NOT NULL,
    user_id                     UUID            NOT NULL,
    payment_method              VARCHAR(30)     NOT NULL,
    status                      VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    amount                      NUMERIC(12, 2)  NOT NULL,
    currency                    VARCHAR(10)     NOT NULL DEFAULT 'INR',
    gateway_transaction_id      VARCHAR(100),
    gateway_response_code       VARCHAR(500),
    failure_reason              TEXT,

    -- Auditing
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by                  VARCHAR(100),
    updated_by                  VARCHAR(100),
    is_deleted                  BOOLEAN         NOT NULL DEFAULT false,
    version                     BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_payment_transactions PRIMARY KEY (id)
);

CREATE INDEX idx_payments_order_id ON payments.transactions(order_id);
CREATE INDEX idx_payments_user_id ON payments.transactions(user_id);
CREATE INDEX idx_payments_status ON payments.transactions(status);
CREATE INDEX idx_payments_gateway_tx_id ON payments.transactions(gateway_transaction_id);
