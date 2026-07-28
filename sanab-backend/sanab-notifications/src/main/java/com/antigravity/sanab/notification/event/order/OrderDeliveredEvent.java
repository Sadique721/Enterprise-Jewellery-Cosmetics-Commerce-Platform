package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when order is successfully delivered. Triggers: Email, SMS, WhatsApp, In-App. */
public record OrderDeliveredEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, Instant deliveredAt,
        String reviewUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public OrderDeliveredEvent(UUID userId, String email, String phone, String firstName,
                               UUID orderId, String orderNumber, String reviewUrl) {
        this(userId, email, phone, firstName, orderId, orderNumber, Instant.now(), reviewUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ORDER_DELIVERED; }
}
