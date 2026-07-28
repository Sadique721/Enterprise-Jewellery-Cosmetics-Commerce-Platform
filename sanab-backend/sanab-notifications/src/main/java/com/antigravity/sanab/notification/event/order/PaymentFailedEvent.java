package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a payment attempt fails.
 * Priority: CRITICAL — user must know immediately to retry payment.
 *
 * @param userId         customer's user ID
 * @param email          customer's email
 * @param phone          customer's phone (nullable)
 * @param firstName      customer's first name
 * @param orderId        the associated order UUID
 * @param orderNumber    human-readable order number
 * @param attemptedAmount the amount that failed to process
 * @param currency       ISO 4217 currency code
 * @param failureReason  provider-supplied reason (sanitized for display)
 * @param retryUrl       URL to retry the payment
 * @param supportUrl     URL to contact support
 * @param occurredAt     event creation timestamp
 */
public record PaymentFailedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID orderId,
        String orderNumber,
        BigDecimal attemptedAmount,
        String currency,
        String failureReason,
        String retryUrl,
        String supportUrl,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PaymentFailedEvent(UUID userId, String email, String phone, String firstName,
                              UUID orderId, String orderNumber, BigDecimal attemptedAmount,
                              String currency, String failureReason,
                              String retryUrl, String supportUrl) {
        this(userId, email, phone, firstName, orderId, orderNumber, attemptedAmount,
                currency, failureReason, retryUrl, supportUrl, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PAYMENT_FAILED;
    }
}
