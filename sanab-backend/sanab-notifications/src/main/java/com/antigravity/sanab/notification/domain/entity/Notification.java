package com.antigravity.sanab.notification.domain.entity;

import com.antigravity.sanab.notification.domain.enums.*;
import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single notification delivery record.
 *
 * <p>Every notification sent by the SANAB platform is persisted as a
 * {@link Notification} entity. This provides:
 * <ul>
 *   <li>Full delivery lifecycle tracking (QUEUED → SENT → DELIVERED → READ)</li>
 *   <li>Retry metadata (retry count, next retry time)</li>
 *   <li>Provider response storage for debugging</li>
 *   <li>Audit trail compliant with SANAB audit requirements</li>
 * </ul>
 *
 * <p>Database: {@code notifications.notifications}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
        name = "notifications",
        schema = "notifications",
        indexes = {
                @Index(name = "idx_notif_user_id", columnList = "user_id"),
                @Index(name = "idx_notif_status", columnList = "status"),
                @Index(name = "idx_notif_event_type", columnList = "event_type"),
                @Index(name = "idx_notif_channel", columnList = "channel"),
                @Index(name = "idx_notif_user_read", columnList = "user_id, is_read"),
                @Index(name = "idx_notif_scheduled", columnList = "scheduled_at"),
                @Index(name = "idx_notif_retry", columnList = "next_retry_at")
        }
)
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    // ─── Target ──────────────────────────────────────────────────────────────

    /**
     * The user this notification belongs to.
     * May be null for broadcast admin notifications.
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * The physical destination address:
     * <ul>
     *   <li>EMAIL: email address</li>
     *   <li>SMS: E.164 phone number</li>
     *   <li>WHATSAPP: E.164 phone number</li>
     *   <li>IN_APP: user UUID (string form)</li>
     * </ul>
     */
    @Column(name = "recipient_address", nullable = false, length = 255)
    private String recipientAddress;

    /** Recipient's display name for personalization. */
    @Column(name = "recipient_name", length = 150)
    private String recipientName;

    // ─── Classification ───────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 60)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private NotificationCategory category;

    // ─── Content ──────────────────────────────────────────────────────────────

    /** Reference to the template used to render this notification. */
    @Column(name = "template_id")
    private UUID templateId;

    /** Email subject line; null for non-email channels. */
    @Column(name = "subject", length = 300)
    private String subject;

    /** Rendered final content sent to the recipient. */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Locale used for rendering (BCP 47 tag, e.g. "en", "ar", "hi").
     * Defaults to "en".
     */
    @Column(name = "locale", nullable = false, length = 10)
    @Builder.Default
    private String locale = "en";

    /**
     * Additional context data as JSON (e.g. orderId, trackingUrl).
     * Stored for audit and debugging purposes.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // ─── Delivery Lifecycle ───────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.QUEUED;

    /** When the notification was submitted to the external provider. */
    @Column(name = "sent_at")
    private Instant sentAt;

    /** When the provider confirmed delivery to the device/inbox. */
    @Column(name = "delivered_at")
    private Instant deliveredAt;

    /** When the user opened/read the notification. */
    @Column(name = "read_at")
    private Instant readAt;

    /** Convenience flag for in-app unread count queries. */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    /** For scheduled notifications: when the notification should be dispatched. */
    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    // ─── Retry ───────────────────────────────────────────────────────────────

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private int retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    /** Timestamp of the next scheduled retry attempt. */
    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    /** Human-readable failure reason from the last failed attempt. */
    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    // ─── Provider ────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 30)
    private ProviderType providerType;

    /** Message SID / message ID returned by the provider. */
    @Column(name = "provider_message_id", length = 255)
    private String providerMessageId;

    /**
     * Raw JSON response from the provider.
     * Useful for debugging delivery failures.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "provider_response", columnDefinition = "jsonb")
    private String providerResponse;

    // ─── Domain Behaviour ────────────────────────────────────────────────────

    /**
     * Marks this notification as successfully sent by the provider.
     *
     * @param providerMessageId the message ID returned by the provider
     * @param providerType      the provider that sent it
     */
    public void markSent(String providerMessageId, ProviderType providerType) {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
        this.providerMessageId = providerMessageId;
        this.providerType = providerType;
        this.failureReason = null;
        this.nextRetryAt = null;
    }

    /**
     * Marks this notification as delivered (provider webhook confirmation).
     */
    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = Instant.now();
    }

    /**
     * Marks this notification as read by the recipient.
     */
    public void markRead() {
        if (!this.read) {
            this.read = true;
            this.readAt = Instant.now();
            this.status = NotificationStatus.READ;
        }
    }

    /**
     * Records a failed delivery attempt and calculates the next retry time.
     *
     * @param reason       human-readable failure reason
     * @param nextRetryAt  when to retry; null if max retries exceeded
     */
    public void recordFailure(String reason, Instant nextRetryAt) {
        this.retryCount++;
        this.failureReason = reason;
        this.status = (nextRetryAt != null)
                ? NotificationStatus.FAILED
                : NotificationStatus.PERMANENTLY_FAILED;
        this.nextRetryAt = nextRetryAt;
    }

    /**
     * Returns whether this notification can still be retried.
     */
    public boolean isRetryable() {
        return retryCount < maxRetries
                && status == NotificationStatus.FAILED;
    }
}
