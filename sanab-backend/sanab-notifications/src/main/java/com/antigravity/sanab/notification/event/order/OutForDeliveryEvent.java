package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when order is out for delivery. Triggers: SMS, WhatsApp, In-App. */
public record OutForDeliveryEvent(
        UUID userId, String email, String phone, String firstName,
        UUID orderId, String orderNumber, String deliveryAgentName,
        String deliveryAgentPhone, String trackingUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public OutForDeliveryEvent(UUID userId, String email, String phone, String firstName,
                               UUID orderId, String orderNumber, String deliveryAgentName,
                               String deliveryAgentPhone, String trackingUrl) {
        this(userId, email, phone, firstName, orderId, orderNumber,
                deliveryAgentName, deliveryAgentPhone, trackingUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.OUT_FOR_DELIVERY; }
}
