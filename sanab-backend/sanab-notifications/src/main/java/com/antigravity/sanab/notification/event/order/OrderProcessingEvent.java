package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when warehouse starts processing the order. Triggers: Email, WhatsApp. */
public record OrderProcessingEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, String estimatedDispatch, Instant occurredAt
) implements SanabNotificationEvent {
    public OrderProcessingEvent(UUID userId, String email, String phone, String firstName,
                                UUID orderId, String orderNumber, String estimatedDispatch) {
        this(userId, email, phone, firstName, orderId, orderNumber, estimatedDispatch, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ORDER_PROCESSING; }
}
