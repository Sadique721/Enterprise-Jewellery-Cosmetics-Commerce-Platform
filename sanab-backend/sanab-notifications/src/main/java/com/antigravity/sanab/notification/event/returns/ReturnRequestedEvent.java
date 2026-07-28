package com.antigravity.sanab.notification.event.returns;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when a return request is submitted. Triggers: Email, SMS, WhatsApp, In-App. */
public record ReturnRequestedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, UUID returnRequestId,
        String returnReason, String expectedResolutionDays, Instant occurredAt
) implements SanabNotificationEvent {
    public ReturnRequestedEvent(UUID userId, String email, String phone, String firstName,
                                UUID orderId, String orderNumber, UUID returnRequestId,
                                String returnReason, String expectedResolutionDays) {
        this(userId, email, phone, firstName, orderId, orderNumber,
                returnRequestId, returnReason, expectedResolutionDays, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.RETURN_REQUESTED; }
}
