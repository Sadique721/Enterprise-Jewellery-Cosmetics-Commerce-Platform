-- ═══════════════════════════════════════════════════════════════════════════
-- V11 — Promotions Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS promotions;

-- ─────────────────────────────────────────────────────────────────────────────
-- COUPONS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE promotions.coupons (
    id                          UUID            NOT NULL DEFAULT gen_random_uuid(),
    code                        VARCHAR(50)     NOT NULL,
    description                 VARCHAR(250),
    discount_type               VARCHAR(30)     NOT NULL,
    discount_value              NUMERIC(12, 2)  NOT NULL,
    minimum_spend               NUMERIC(12, 2),
    maximum_discount_amount     NUMERIC(12, 2),
    usage_limit                 INT,
    used_count                  INT             NOT NULL DEFAULT 0,
    valid_from                  TIMESTAMPTZ,
    valid_until                 TIMESTAMPTZ,
    is_active                   BOOLEAN         NOT NULL DEFAULT true,

    -- Auditing
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by                  VARCHAR(100),
    updated_by                  VARCHAR(100),
    is_deleted                  BOOLEAN         NOT NULL DEFAULT false,
    version                     BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_promotions_coupons PRIMARY KEY (id),
    CONSTRAINT uq_coupons_code UNIQUE (code)
);

CREATE INDEX idx_coupons_code ON promotions.coupons(code);
CREATE INDEX idx_coupons_active ON promotions.coupons(is_active);
