package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Abandoned cart reminder. Triggers: Email, WhatsApp, In-App. */
public record AbandonedCartEvent(
        UUID userId, String email, String phone, String firstName,
        List<String> cartItemNames, BigDecimal cartTotal, String currency,
        String cartUrl, String discountCode, Instant occurredAt
) implements SanabNotificationEvent {
    public AbandonedCartEvent(UUID userId, String email, String phone, String firstName,
                              List<String> cartItemNames, BigDecimal cartTotal,
                              String currency, String cartUrl, String discountCode) {
        this(userId, email, phone, firstName, cartItemNames, cartTotal,
                currency, cartUrl, discountCode, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ABANDONED_CART; }
}
