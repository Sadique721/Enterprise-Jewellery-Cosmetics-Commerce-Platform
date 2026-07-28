-- ═══════════════════════════════════════════════════════════════════════════
-- V13 — CMS Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS cms;

-- ─────────────────────────────────────────────────────────────────────────────
-- BANNERS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE cms.banners (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    title           VARCHAR(150)    NOT NULL,
    subtitle        VARCHAR(300),
    image_url       VARCHAR(500)    NOT NULL,
    target_url      VARCHAR(500),
    position        VARCHAR(30)     NOT NULL,
    display_order   INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT true,
    valid_from      TIMESTAMPTZ,
    valid_until     TIMESTAMPTZ,

    -- Auditing
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN         NOT NULL DEFAULT false,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_cms_banners PRIMARY KEY (id)
);

CREATE INDEX idx_banners_position ON cms.banners(position);
CREATE INDEX idx_banners_active ON cms.banners(is_active);
