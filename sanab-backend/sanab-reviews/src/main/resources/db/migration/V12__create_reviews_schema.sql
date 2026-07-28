-- ═══════════════════════════════════════════════════════════════════════════
-- V12 — Reviews Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS reviews;

-- ─────────────────────────────────────────────────────────────────────────────
-- REVIEWS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE reviews.reviews (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    product_id              UUID            NOT NULL,
    user_id                 UUID            NOT NULL,
    reviewer_name           VARCHAR(100)    NOT NULL,
    rating                  INT             NOT NULL,
    title                   VARCHAR(200),
    comment                 TEXT            NOT NULL,
    status                  VARCHAR(20)     NOT NULL DEFAULT 'APPROVED',
    is_verified_purchase    BOOLEAN         NOT NULL DEFAULT false,

    -- Auditing
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    is_deleted              BOOLEAN         NOT NULL DEFAULT false,
    version                 BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_reviews PRIMARY KEY (id)
);

CREATE INDEX idx_reviews_product_id ON reviews.reviews(product_id);
CREATE INDEX idx_reviews_user_id ON reviews.reviews(user_id);
CREATE INDEX idx_reviews_status ON reviews.reviews(status);
