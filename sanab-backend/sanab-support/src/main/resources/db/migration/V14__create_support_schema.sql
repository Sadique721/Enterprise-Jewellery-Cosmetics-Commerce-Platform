-- ═══════════════════════════════════════════════════════════════════════════
-- V14 — Support Schema
-- SANAB Enterprise Commerce Platform
-- Organization: Antigravity Technology
-- ═══════════════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS support;

-- ─────────────────────────────────────────────────────────────────────────────
-- TICKETS
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE support.tickets (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    ticket_number   VARCHAR(50)     NOT NULL,
    user_id         UUID            NOT NULL,
    order_id        UUID,
    subject         VARCHAR(200)    NOT NULL,
    description     TEXT            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    priority        VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM',

    -- Auditing
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN         NOT NULL DEFAULT false,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_support_tickets PRIMARY KEY (id),
    CONSTRAINT uq_tickets_number UNIQUE (ticket_number)
);

CREATE INDEX idx_tickets_number ON support.tickets(ticket_number);
CREATE INDEX idx_tickets_user_id ON support.tickets(user_id);
CREATE INDEX idx_tickets_status ON support.tickets(status);
