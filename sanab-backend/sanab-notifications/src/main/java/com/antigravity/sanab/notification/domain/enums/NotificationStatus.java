package com.antigravity.sanab.notification.domain.enums;

/**
 * Lifecycle status of a single notification delivery attempt.
 *
 * <p>State transitions:
 * <pre>
 *   QUEUED → PROCESSING → SENT → DELIVERED → READ
 *                       ↘ FAILED → (retry) → SENT | PERMANENTLY_FAILED
 *   QUEUED → CANCELLED (user preference disabled before processing)
 * </pre>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public enum NotificationStatus {

    /** Notification has been created and is waiting to be processed. */
    QUEUED,

    /** Notification is currently being processed by the dispatcher. */
    PROCESSING,

    /** Notification was submitted to the external provider successfully. */
    SENT,

    /** Provider confirmed delivery to the recipient's device/inbox. */
    DELIVERED,

    /** Recipient has read/opened the notification. */
    READ,

    /** Delivery attempt failed; may be retried based on retry policy. */
    FAILED,

    /** All retry attempts exhausted; manual intervention may be required. */
    PERMANENTLY_FAILED,

    /** Notification was cancelled (e.g., user preference was disabled). */
    CANCELLED,

    /** Notification is scheduled for future delivery; not yet dispatched. */
    SCHEDULED
}
