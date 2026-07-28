package com.antigravity.sanab.notification.domain.enums;

/**
 * High-level category grouping for notification preferences.
 *
 * <p>Users control notifications at the category level via their
 * {@link com.antigravity.sanab.notification.domain.entity.NotificationPreference}.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public enum NotificationCategory {

    /** Account security: login alerts, password changes, device notifications. */
    SECURITY,

    /** Transactional: orders, payments, shipping, returns, refunds. */
    TRANSACTIONAL,

    /** Promotional: coupons, flash sales, loyalty rewards, seasonal offers. */
    MARKETING,

    /** Product alerts: price drops, back-in-stock, wishlist updates. */
    PRODUCT_ALERTS,

    /** Platform-wide: system maintenance, newsletters. */
    SYSTEM,

    /** Administrative alerts for staff (inventory low, high-value orders, etc.). */
    ADMIN
}
