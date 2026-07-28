package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a coupon is issued to a specific user.
 * Triggers: Email, SMS (configurable), WhatsApp, In-App.
 */
public record CouponIssuedEvent(
        UUID userId, String email, String phone, String firstName,
        String couponCode, String discountDescription, BigDecimal minimumOrderValue,
        String currency, Instant validUntil, String shopUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public CouponIssuedEvent(UUID userId, String email, String phone, String firstName,
                             String couponCode, String discountDescription,
                             BigDecimal minimumOrderValue, String currency,
                             Instant validUntil, String shopUrl) {
        this(userId, email, phone, firstName, couponCode, discountDescription,
                minimumOrderValue, currency, validUntil, shopUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.COUPON_ISSUED; }
}
