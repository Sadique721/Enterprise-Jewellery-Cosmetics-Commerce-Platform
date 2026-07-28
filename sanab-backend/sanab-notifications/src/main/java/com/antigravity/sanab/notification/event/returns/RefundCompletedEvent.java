package com.antigravity.sanab.notification.event.returns;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Published when a refund is confirmed credited back. Triggers: Email, SMS, WhatsApp, In-App. */
public record RefundCompletedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, String refundId,
        BigDecimal refundAmount, String currency, Instant creditedAt, Instant occurredAt
) implements SanabNotificationEvent {
    public RefundCompletedEvent(UUID userId, String email, String phone, String firstName,
                                UUID orderId, String orderNumber, String refundId,
                                BigDecimal refundAmount, String currency) {
        this(userId, email, phone, firstName, orderId, orderNumber, refundId,
                refundAmount, currency, Instant.now(), Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.REFUND_COMPLETED; }
}
