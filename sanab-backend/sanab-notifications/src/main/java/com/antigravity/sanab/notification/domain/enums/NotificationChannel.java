package com.antigravity.sanab.notification.domain.enums;

/**
 * Represents the delivery channels through which notifications are dispatched.
 *
 * <p>Used throughout the notification pipeline to route notifications
 * to the appropriate channel adapter.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public enum NotificationChannel {

    /** HTML email with optional PDF attachments sent via SMTP or API. */
    EMAIL,

    /** Short text message sent via configured SMS provider (Twilio or MSG91). */
    SMS,

    /** WhatsApp message sent via Meta Cloud API or Twilio WhatsApp. */
    WHATSAPP,

    /** Persistent in-application notification stored and surfaced via API. */
    IN_APP
}
