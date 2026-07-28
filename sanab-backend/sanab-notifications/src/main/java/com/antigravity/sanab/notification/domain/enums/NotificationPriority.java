package com.antigravity.sanab.notification.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Delivery priority level for notifications.
 *
 * <p>Determines queue ordering and retry aggressiveness:
 * <ul>
 *   <li>{@link #CRITICAL} — Security alerts, OTPs; immediate delivery required</li>
 *   <li>{@link #HIGH} — Order confirmations, payments; near-immediate delivery</li>
 *   <li>{@link #NORMAL} — Shipping updates, general transactional</li>
 *   <li>{@link #LOW} — Promotional, marketing; best-effort</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum NotificationPriority {

    /** Security-critical (OTP, account lock, suspicious login). Deliver immediately. */
    CRITICAL(1, 3, 0),

    /** Business-critical (order placed, payment success). Deliver within seconds. */
    HIGH(2, 3, 30),

    /** Standard transactional (shipping update, review request). */
    NORMAL(3, 3, 300),

    /** Marketing/promotional. Retry less aggressively. */
    LOW(4, 2, 3600);

    /** Numeric priority (lower = higher urgency). */
    private final int level;

    /** Maximum retry attempts for this priority tier. */
    private final int maxRetries;

    /** Initial retry delay in seconds (before exponential backoff). */
    private final int initialDelaySeconds;
}
