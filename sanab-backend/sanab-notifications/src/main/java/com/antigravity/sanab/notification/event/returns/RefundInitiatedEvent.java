package com.antigravity.sanab.notification.event.returns;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Published when a refund is initiated by the platform. Triggers: Email, SMS, WhatsApp, In-App. */
public record RefundInitiatedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, String refundId,
        BigDecimal refundAmount, String currency, String refundMethod,
        String estimatedArrival, Instant occurredAt
) implements SanabNotificationEvent {
    public RefundInitiatedEvent(UUID userId, String email, String phone, String firstName,
                                UUID orderId, String orderNumber, String refundId,
                                BigDecimal refundAmount, String currency,
                                String refundMethod, String estimatedArrival) {
        this(userId, email, phone, firstName, orderId, orderNumber, refundId,
                refundAmount, currency, refundMethod, estimatedArrival, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.REFUND_INITIATED; }
}
