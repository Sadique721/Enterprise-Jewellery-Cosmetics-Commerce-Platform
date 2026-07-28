package com.antigravity.sanab.notification.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Stores a user's per-channel, per-category notification preferences.
 *
 * <p>Each user has exactly one {@link NotificationPreference} record.
 * The service creates a default record (all enabled) on first access
 * if none exists.
 *
 * <p>Users can independently toggle:
 * <ul>
 *   <li>Each channel (Email, SMS, WhatsApp, Push, In-App)</li>
 *   <li>Each category (Security, Transactional, Marketing, etc.)</li>
 * </ul>
 *
 * <p><strong>Security notifications cannot be disabled</strong> — they are
 * always delivered regardless of preferences.
 *
 * <p>Database: {@code notifications.notification_preferences}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
        name = "notification_preferences",
        schema = "notifications",
        indexes = {
                @Index(name = "idx_notif_pref_user_id", columnList = "user_id", unique = true)
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference extends BaseEntity {

    /** The user these preferences belong to. One-to-one relationship. */
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    // ─── Channel Preferences ─────────────────────────────────────────────────

    /**
     * Whether the user wants to receive email notifications.
     * Always true for security categories regardless of this flag.
     */
    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private boolean emailEnabled = true;

    /** Whether the user wants to receive SMS notifications. */
    @Column(name = "sms_enabled", nullable = false)
    @Builder.Default
    private boolean smsEnabled = true;

    /** Whether the user wants to receive WhatsApp notifications. */
    @Column(name = "whatsapp_enabled", nullable = false)
    @Builder.Default
    private boolean whatsAppEnabled = true;

    /** Whether the user wants to receive push notifications (future use). */
    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private boolean pushEnabled = true;

    /** Whether the user wants to see in-app notifications. */
    @Column(name = "in_app_enabled", nullable = false)
    @Builder.Default
    private boolean inAppEnabled = true;

    // ─── Category Preferences ────────────────────────────────────────────────

    /**
     * Security notifications (login alerts, password changes).
     * This field is informational only — security notifications are
     * ALWAYS delivered regardless of this setting.
     */
    @Column(name = "security_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean securityNotificationsEnabled = true;

    /**
     * Order, payment, shipping, and returns notifications.
     * These are strongly recommended — users should understand
     * the risk of disabling them.
     */
    @Column(name = "transactional_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean transactionalNotificationsEnabled = true;

    /** Coupons, flash sales, festival offers, and promotional campaigns. */
    @Column(name = "marketing_enabled", nullable = false)
    @Builder.Default
    private boolean marketingEnabled = true;

    /** Price drop and back-in-stock alerts for wishlisted items. */
    @Column(name = "product_alerts_enabled", nullable = false)
    @Builder.Default
    private boolean productAlertsEnabled = true;

    /** System maintenance and platform announcements. */
    @Column(name = "system_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean systemNotificationsEnabled = true;

    /** Newsletter subscription. */
    @Column(name = "newsletter_enabled", nullable = false)
    @Builder.Default
    private boolean newsletterEnabled = true;

    // ─── Domain Behaviour ────────────────────────────────────────────────────

    /**
     * Factory method creating a fully-enabled default preference for a new user.
     */
    public static NotificationPreference defaultFor(UUID userId) {
        return NotificationPreference.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(true)
                .whatsAppEnabled(true)
                .pushEnabled(true)
                .inAppEnabled(true)
                .securityNotificationsEnabled(true)
                .transactionalNotificationsEnabled(true)
                .marketingEnabled(true)
                .productAlertsEnabled(true)
                .systemNotificationsEnabled(true)
                .newsletterEnabled(true)
                .build();
    }
}
