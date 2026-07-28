package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** High-value order placed (above configured threshold). Priority: HIGH. */
public record HighValueOrderEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, UUID customerId, String customerEmail,
        BigDecimal orderAmount, String currency, Instant occurredAt
) implements SanabNotificationEvent {
    public HighValueOrderEvent(UUID orderId, String orderNumber, UUID customerId,
                               String customerEmail, BigDecimal orderAmount, String currency) {
        this(null, null, null, "Admin", orderId, orderNumber,
                customerId, customerEmail, orderAmount, currency, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.HIGH_VALUE_ORDER; }
}
