package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Published when an order is cancelled. Triggers: Email, SMS, WhatsApp, In-App. */
public record OrderCancelledEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, String cancellationReason,
        BigDecimal refundAmount, String currency, String refundTimeline,
        Instant occurredAt
) implements SanabNotificationEvent {
    public OrderCancelledEvent(UUID userId, String email, String phone, String firstName,
                               UUID orderId, String orderNumber, String cancellationReason,
                               BigDecimal refundAmount, String currency, String refundTimeline) {
        this(userId, email, phone, firstName, orderId, orderNumber,
                cancellationReason, refundAmount, currency, refundTimeline, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ORDER_CANCELLED; }
}
