package com.antigravity.sanab.notification.event.returns;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when return pickup is scheduled. Triggers: Email, SMS, WhatsApp, In-App. */
public record PickupScheduledEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, Instant pickupDate,
        String pickupTimeSlot, String courierName, Instant occurredAt
) implements SanabNotificationEvent {
    public PickupScheduledEvent(UUID userId, String email, String phone, String firstName,
                                UUID orderId, String orderNumber, Instant pickupDate,
                                String pickupTimeSlot, String courierName) {
        this(userId, email, phone, firstName, orderId, orderNumber, pickupDate,
                pickupTimeSlot, courierName, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.PICKUP_SCHEDULED; }
}
