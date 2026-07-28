package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a payment is successfully processed.
 *
 * @param userId            customer's user ID
 * @param email             customer's email
 * @param phone             customer's phone (nullable)
 * @param firstName         customer's first name
 * @param orderId           the associated order UUID
 * @param orderNumber       human-readable order number
 * @param transactionId     payment gateway transaction ID
 * @param amountPaid        the amount charged
 * @param currency          ISO 4217 currency code
 * @param paymentMethod     method used (e.g. "UPI", "Credit Card")
 * @param receiptUrl        URL to download payment receipt
 * @param occurredAt        event creation timestamp
 */
public record PaymentSuccessfulEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID orderId,
        String orderNumber,
        String transactionId,
        BigDecimal amountPaid,
        String currency,
        String paymentMethod,
        String receiptUrl,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PaymentSuccessfulEvent(UUID userId, String email, String phone, String firstName,
                                  UUID orderId, String orderNumber, String transactionId,
                                  BigDecimal amountPaid, String currency,
                                  String paymentMethod, String receiptUrl) {
        this(userId, email, phone, firstName, orderId, orderNumber, transactionId,
                amountPaid, currency, paymentMethod, receiptUrl, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PAYMENT_SUCCESSFUL;
    }
}
