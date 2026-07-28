package com.antigravity.sanab.notification.domain.enums;

/**
 * Identifies the external provider implementation for a given channel.
 *
 * <p>Used to select the correct provider at runtime and for audit logging.
 * Multiple providers are supported per channel (adapter pattern).
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public enum ProviderType {

    // ─── Email Providers ──────────────────────────────────────────────────────
    /** Standard SMTP (JavaMail). Works with any SMTP server or relay. */
    SMTP,

    // ─── SMS Providers ────────────────────────────────────────────────────────
    /** Twilio Programmable Messaging for SMS. */
    TWILIO_SMS,

    /** MSG91 SMS gateway. */
    MSG91,

    /** Textlocal SMS gateway. */
    TEXTLOCAL,

    // ─── WhatsApp Providers ───────────────────────────────────────────────────
    /** Meta WhatsApp Business Cloud API (official). */
    META_WHATSAPP,

    /** Twilio WhatsApp integration. */
    TWILIO_WHATSAPP,

    // ─── In-App ───────────────────────────────────────────────────────────────
    /** Internal database-backed in-app notification storage. */
    IN_APP_INTERNAL
}
