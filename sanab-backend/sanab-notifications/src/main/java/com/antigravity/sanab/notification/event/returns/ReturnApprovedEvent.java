package com.antigravity.sanab.notification.event.returns;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when a return is approved. Triggers: Email, SMS, WhatsApp, In-App. */
public record ReturnApprovedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, UUID returnRequestId,
        String nextStep, Instant occurredAt
) implements SanabNotificationEvent {
    public ReturnApprovedEvent(UUID userId, String email, String phone, String firstName,
                               UUID orderId, String orderNumber, UUID returnRequestId, String nextStep) {
        this(userId, email, phone, firstName, orderId, orderNumber, returnRequestId, nextStep, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.RETURN_APPROVED; }
}
