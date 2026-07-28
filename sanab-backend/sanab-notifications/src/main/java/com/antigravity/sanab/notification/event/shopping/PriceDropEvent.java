package com.antigravity.sanab.notification.event.shopping;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a product on a user's wishlist experiences a price drop.
 *
 * <p>Triggers: Price Drop Email, WhatsApp, In-App Alert.
 *
 * @param userId         the user with the wishlisted product
 * @param email          user's email
 * @param phone          user's phone (nullable)
 * @param firstName      user's first name
 * @param productId      the product that dropped in price
 * @param productName    product display name
 * @param productSlug    URL slug for deep linking
 * @param productImageUrl thumbnail image URL
 * @param originalPrice  the previous price
 * @param newPrice       the new discounted price
 * @param currency       ISO 4217 currency code (e.g. "INR")
 * @param discountPercent calculated percentage discount
 * @param productUrl     direct URL to the product page
 * @param occurredAt     event creation timestamp
 */
public record PriceDropEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID productId,
        String productName,
        String productSlug,
        String productImageUrl,
        BigDecimal originalPrice,
        BigDecimal newPrice,
        String currency,
        int discountPercent,
        String productUrl,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PriceDropEvent(UUID userId, String email, String phone, String firstName,
                          UUID productId, String productName, String productSlug,
                          String productImageUrl, BigDecimal originalPrice,
                          BigDecimal newPrice, String currency, String productUrl) {
        this(userId, email, phone, firstName, productId, productName, productSlug,
                productImageUrl, originalPrice, newPrice, currency,
                calculateDiscount(originalPrice, newPrice), productUrl, Instant.now());
    }

    private static int calculateDiscount(BigDecimal original, BigDecimal newPrice) {
        if (original == null || original.signum() == 0) return 0;
        return original.subtract(newPrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(original, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PRICE_DROP;
    }
}
