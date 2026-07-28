package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when order is packed and ready for pickup by courier. Triggers: Email, SMS, WhatsApp. */
public record OrderPackedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, Instant occurredAt
) implements SanabNotificationEvent {
    public OrderPackedEvent(UUID userId, String email, String phone, String firstName,
                            UUID orderId, String orderNumber) {
        this(userId, email, phone, firstName, orderId, orderNumber, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ORDER_PACKED; }
}
