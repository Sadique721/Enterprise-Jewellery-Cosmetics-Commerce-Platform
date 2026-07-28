package com.antigravity.sanab.notification.event.returns;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when a return request is rejected. Triggers: Email, SMS, WhatsApp, In-App. */
public record ReturnRejectedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, UUID returnRequestId,
        String rejectionReason, String appealUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public ReturnRejectedEvent(UUID userId, String email, String phone, String firstName,
                               UUID orderId, String orderNumber, UUID returnRequestId,
                               String rejectionReason, String appealUrl) {
        this(userId, email, phone, firstName, orderId, orderNumber, returnRequestId,
                rejectionReason, appealUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.RETURN_REJECTED; }
}
