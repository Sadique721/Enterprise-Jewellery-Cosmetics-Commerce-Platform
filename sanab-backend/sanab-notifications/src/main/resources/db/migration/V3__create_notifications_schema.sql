-- ═══════════════════════════════════════════════════════════════════════════════
-- SANAB :: Notification Module Schema
-- Migration: V3__create_notifications_schema.sql
-- Description: Creates the notifications schema and all notification tables
-- Author: Antigravity Engineering
-- ═══════════════════════════════════════════════════════════════════════════════

-- ─── Schema ───────────────────────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS notifications;

COMMENT ON SCHEMA notifications IS 'SANAB Notification Module: notification records, templates, and user preferences';

-- ─── Notification Templates ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications.notification_templates (
    id                  UUID                     NOT NULL DEFAULT gen_random_uuid(),
    name                VARCHAR(150)             NOT NULL,
    event_type          VARCHAR(60)              NOT NULL,
    channel             VARCHAR(20)              NOT NULL,
    locale              VARCHAR(10)              NOT NULL DEFAULT 'en',
    version             INTEGER                  NOT NULL DEFAULT 1,
    is_active           BOOLEAN                  NOT NULL DEFAULT TRUE,
    subject_template    VARCHAR(300),
    body_template       TEXT                     NOT NULL,
    template_file_path  VARCHAR(300),
    description         VARCHAR(500),

    -- Audit columns (from BaseEntity)
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    version_no          BIGINT                   NOT NULL DEFAULT 0,
    deleted             BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP WITH TIME ZONE,
    deleted_by          VARCHAR(100),

    CONSTRAINT pk_notification_templates PRIMARY KEY (id),
    CONSTRAINT uq_template_event_channel_locale_version
        UNIQUE (event_type, channel, locale, version)
);

CREATE INDEX IF NOT EXISTS idx_tmpl_event_channel_locale
    ON notifications.notification_templates (event_type, channel, locale);

CREATE INDEX IF NOT EXISTS idx_tmpl_active
    ON notifications.notification_templates (event_type, channel, locale)
    WHERE is_active = TRUE;

COMMENT ON TABLE notifications.notification_templates IS 'Versioned Thymeleaf templates for all notification channels';

-- ─── Notification Preferences ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications.notification_preferences (
    id                                   UUID                     NOT NULL DEFAULT gen_random_uuid(),
    user_id                              UUID                     NOT NULL,

    -- Channel preferences
    email_enabled                        BOOLEAN                  NOT NULL DEFAULT TRUE,
    sms_enabled                          BOOLEAN                  NOT NULL DEFAULT TRUE,
    whatsapp_enabled                     BOOLEAN                  NOT NULL DEFAULT TRUE,
    push_enabled                         BOOLEAN                  NOT NULL DEFAULT TRUE,
    in_app_enabled                       BOOLEAN                  NOT NULL DEFAULT TRUE,

    -- Category preferences
    security_notifications_enabled       BOOLEAN                  NOT NULL DEFAULT TRUE,
    transactional_notifications_enabled  BOOLEAN                  NOT NULL DEFAULT TRUE,
    marketing_enabled                    BOOLEAN                  NOT NULL DEFAULT TRUE,
    product_alerts_enabled               BOOLEAN                  NOT NULL DEFAULT TRUE,
    system_notifications_enabled         BOOLEAN                  NOT NULL DEFAULT TRUE,
    newsletter_enabled                   BOOLEAN                  NOT NULL DEFAULT TRUE,

    -- Audit
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    version_no          BIGINT                   NOT NULL DEFAULT 0,
    deleted             BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP WITH TIME ZONE,
    deleted_by          VARCHAR(100),

    CONSTRAINT pk_notification_preferences PRIMARY KEY (id),
    CONSTRAINT uq_notif_pref_user_id UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_notif_pref_user_id
    ON notifications.notification_preferences (user_id);

COMMENT ON TABLE notifications.notification_preferences IS 'Per-user notification channel and category preferences';

-- ─── Notifications ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications.notifications (
    id                   UUID                     NOT NULL DEFAULT gen_random_uuid(),
    user_id              UUID,
    recipient_address    VARCHAR(255)             NOT NULL,
    recipient_name       VARCHAR(150),

    -- Classification
    event_type           VARCHAR(60)              NOT NULL,
    channel              VARCHAR(20)              NOT NULL,
    priority             VARCHAR(20)              NOT NULL DEFAULT 'NORMAL',
    category             VARCHAR(30)              NOT NULL,

    -- Content
    template_id          UUID,
    subject              VARCHAR(300),
    content              TEXT,
    locale               VARCHAR(10)              NOT NULL DEFAULT 'en',
    metadata             JSONB,

    -- Delivery lifecycle
    status               VARCHAR(25)              NOT NULL DEFAULT 'QUEUED',
    sent_at              TIMESTAMP WITH TIME ZONE,
    delivered_at         TIMESTAMP WITH TIME ZONE,
    read_at              TIMESTAMP WITH TIME ZONE,
    is_read              BOOLEAN                  NOT NULL DEFAULT FALSE,
    scheduled_at         TIMESTAMP WITH TIME ZONE,

    -- Retry
    retry_count          INTEGER                  NOT NULL DEFAULT 0,
    max_retries          INTEGER                  NOT NULL DEFAULT 3,
    next_retry_at        TIMESTAMP WITH TIME ZONE,
    failure_reason       VARCHAR(1000),

    -- Provider
    provider_type        VARCHAR(30),
    provider_message_id  VARCHAR(255),
    provider_response    JSONB,

    -- Audit
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP WITH TIME ZONE,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    version_no           BIGINT                   NOT NULL DEFAULT 0,
    deleted              BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP WITH TIME ZONE,
    deleted_by           VARCHAR(100),

    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

-- Performance indexes (aligned with repository query patterns)
CREATE INDEX IF NOT EXISTS idx_notif_user_id       ON notifications.notifications (user_id);
CREATE INDEX IF NOT EXISTS idx_notif_status        ON notifications.notifications (status);
CREATE INDEX IF NOT EXISTS idx_notif_event_type    ON notifications.notifications (event_type);
CREATE INDEX IF NOT EXISTS idx_notif_channel       ON notifications.notifications (channel);
CREATE INDEX IF NOT EXISTS idx_notif_user_read     ON notifications.notifications (user_id, is_read)
    WHERE channel = 'IN_APP';

-- Partial indexes for specific query patterns
CREATE INDEX IF NOT EXISTS idx_notif_scheduled     ON notifications.notifications (scheduled_at)
    WHERE scheduled_at IS NOT NULL AND status = 'SCHEDULED';

CREATE INDEX IF NOT EXISTS idx_notif_retry         ON notifications.notifications (next_retry_at, priority)
    WHERE status = 'FAILED' AND next_retry_at IS NOT NULL;

-- Composite index for inbox query (userId + channel + createdAt DESC)
CREATE INDEX IF NOT EXISTS idx_notif_inbox         ON notifications.notifications (user_id, channel, created_at DESC)
    WHERE deleted = FALSE;

COMMENT ON TABLE notifications.notifications IS 'Delivery records for all notification events across all channels';
COMMENT ON COLUMN notifications.notifications.metadata IS 'Serialized event payload for audit and debugging';
COMMENT ON COLUMN notifications.notifications.provider_response IS 'Raw provider API response for debugging failed deliveries';
