package com.antigravity.sanab.notification.event.shopping;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a previously out-of-stock product becomes available.
 *
 * <p>Triggered for users who opted into stock alerts for this product.
 * Triggers: Email, SMS, WhatsApp, In-App alert.
 *
 * @param userId         user who requested the stock alert
 * @param email          user's email
 * @param phone          user's phone (nullable)
 * @param firstName      user's first name
 * @param productId      the product that is back in stock
 * @param productName    product display name
 * @param productSlug    URL slug
 * @param productImageUrl thumbnail URL
 * @param currentPrice   current selling price
 * @param currency       ISO 4217 currency code
 * @param availableQty   currently available quantity
 * @param productUrl     direct product URL
 * @param occurredAt     event creation timestamp
 */
public record BackInStockEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID productId,
        String productName,
        String productSlug,
        String productImageUrl,
        BigDecimal currentPrice,
        String currency,
        int availableQty,
        String productUrl,
        Instant occurredAt
) implements SanabNotificationEvent {

    public BackInStockEvent(UUID userId, String email, String phone, String firstName,
                            UUID productId, String productName, String productSlug,
                            String productImageUrl, BigDecimal currentPrice, String currency,
                            int availableQty, String productUrl) {
        this(userId, email, phone, firstName, productId, productName, productSlug,
                productImageUrl, currentPrice, currency, availableQty, productUrl, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.BACK_IN_STOCK;
    }
}
