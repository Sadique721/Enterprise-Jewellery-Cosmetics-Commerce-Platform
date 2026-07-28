-- ═══════════════════════════════════════════════════════════════════════════
-- V2 — Catalog Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS catalog;

-- ─────────────────────────────────────────────────────────────────────────────
-- CATEGORIES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE catalog.categories (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    name            VARCHAR(150) NOT NULL,
    slug            VARCHAR(180) NOT NULL,
    description     VARCHAR(500),
    image_url       VARCHAR(500),
    parent_id       UUID,
    display_order   INT         NOT NULL DEFAULT 0,
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    is_featured     BOOLEAN     NOT NULL DEFAULT false,

    -- Auditing
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN     NOT NULL DEFAULT false,
    version         BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES catalog.categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_categories_slug ON catalog.categories(slug);
CREATE INDEX idx_categories_parent_id ON catalog.categories(parent_id);
CREATE INDEX idx_categories_active ON catalog.categories(is_active);

-- ─────────────────────────────────────────────────────────────────────────────
-- BRANDS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE catalog.brands (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    name            VARCHAR(150) NOT NULL,
    slug            VARCHAR(180) NOT NULL,
    logo_url        VARCHAR(500),
    description     TEXT,
    website_url     VARCHAR(200),
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    is_featured     BOOLEAN     NOT NULL DEFAULT false,

    -- Auditing
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN     NOT NULL DEFAULT false,
    version         BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_brands PRIMARY KEY (id),
    CONSTRAINT uq_brands_slug UNIQUE (slug)
);

CREATE INDEX idx_brands_slug ON catalog.brands(slug);
CREATE INDEX idx_brands_active ON catalog.brands(is_active);

-- ─────────────────────────────────────────────────────────────────────────────
-- PRODUCTS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE catalog.products (
    id                          UUID            NOT NULL DEFAULT gen_random_uuid(),
    title                       VARCHAR(200)    NOT NULL,
    slug                        VARCHAR(220)    NOT NULL,
    sku                         VARCHAR(100)    NOT NULL,
    description                 TEXT,
    short_description           VARCHAR(500),
    base_price                  NUMERIC(12, 2)  NOT NULL,
    sale_price                  NUMERIC(12, 2),
    product_type                VARCHAR(30)     NOT NULL,
    status                      VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    category_id                 UUID            NOT NULL,
    brand_id                    UUID,

    -- Jewellery specific
    purity                      VARCHAR(30),
    metal_color                 VARCHAR(30),
    metal_weight_grams          NUMERIC(8, 3),
    gemstone_details            VARCHAR(200),
    hallmarking_certification   VARCHAR(100),

    -- Cosmetics specific
    shade_name                  VARCHAR(100),
    volume_ml                   VARCHAR(50),
    skin_type                   VARCHAR(100),
    ingredient_list             TEXT,

    -- Flags & Inventory
    is_featured                 BOOLEAN         NOT NULL DEFAULT false,
    is_bestseller               BOOLEAN         NOT NULL DEFAULT false,
    total_stock_quantity       INT             NOT NULL DEFAULT 0,

    -- Auditing
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by                  VARCHAR(100),
    updated_by                  VARCHAR(100),
    is_deleted                  BOOLEAN         NOT NULL DEFAULT false,
    version                     BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uq_products_sku UNIQUE (sku),
    CONSTRAINT uq_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES catalog.categories(id),
    CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES catalog.brands(id) ON DELETE SET NULL
);

CREATE INDEX idx_products_slug ON catalog.products(slug);
CREATE INDEX idx_products_type ON catalog.products(product_type);
CREATE INDEX idx_products_status ON catalog.products(status);
CREATE INDEX idx_products_category_id ON catalog.products(category_id);
CREATE INDEX idx_products_brand_id ON catalog.products(brand_id);
CREATE INDEX idx_products_featured ON catalog.products(is_featured);

-- ─────────────────────────────────────────────────────────────────────────────
-- PRODUCT IMAGES
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE catalog.product_images (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    product_id      UUID        NOT NULL,
    url             VARCHAR(500) NOT NULL,
    alt_text        VARCHAR(200),
    display_order   INT         NOT NULL DEFAULT 0,
    is_primary      BOOLEAN     NOT NULL DEFAULT false,

    -- Auditing
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN     NOT NULL DEFAULT false,
    version         BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_product_images PRIMARY KEY (id),
    CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES catalog.products(id) ON DELETE CASCADE
);

CREATE INDEX idx_images_product_id ON catalog.product_images(product_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- PRODUCT VARIANTS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE catalog.product_variants (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    product_id          UUID            NOT NULL,
    sku                 VARCHAR(100)    NOT NULL,
    variant_name        VARCHAR(150)    NOT NULL,
    price_override      NUMERIC(12, 2),
    stock_quantity      INT             NOT NULL DEFAULT 0,
    attribute_name      VARCHAR(100),
    attribute_value     VARCHAR(100),
    is_active           BOOLEAN         NOT NULL DEFAULT true,

    -- Auditing
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN         NOT NULL DEFAULT false,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_product_variants PRIMARY KEY (id),
    CONSTRAINT uq_product_variants_sku UNIQUE (sku),
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES catalog.products(id) ON DELETE CASCADE
);

CREATE INDEX idx_variants_product_id ON catalog.product_variants(product_id);
CREATE INDEX idx_variants_sku ON catalog.product_variants(sku);
CREATE INDEX idx_variants_active ON catalog.product_variants(is_active);
