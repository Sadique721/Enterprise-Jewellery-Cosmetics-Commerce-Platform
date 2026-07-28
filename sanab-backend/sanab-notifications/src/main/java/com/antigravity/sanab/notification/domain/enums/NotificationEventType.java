package com.antigravity.sanab.notification.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

/**
 * Exhaustive enumeration of all business events that can trigger notifications.
 *
 * <p>Each event type declares:
 * <ul>
 *   <li>Its human-readable display name</li>
 *   <li>Its {@link NotificationCategory} for preference management</li>
 *   <li>Its default {@link NotificationPriority}</li>
 *   <li>The set of {@link NotificationChannel}s to use by default</li>
 * </ul>
 *
 * <p>Template keys follow the convention: {@code {eventType.name().toLowerCase()}.{channel}}
 * e.g. {@code user_registered.email}, {@code order_placed.sms}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum NotificationEventType {

    // ═══════════════════════════════════════════════════════════════════════════
    // AUTHENTICATION EVENTS
    // ═══════════════════════════════════════════════════════════════════════════

    USER_REGISTERED(
            "User Registration",
            NotificationCategory.SECURITY,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    EMAIL_VERIFICATION_SENT(
            "Email Verification Link",
            NotificationCategory.SECURITY,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL)
    ),
    EMAIL_VERIFIED(
            "Email Verification Confirmed",
            NotificationCategory.SECURITY,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    PHONE_VERIFIED(
            "Phone Verification Confirmed",
            NotificationCategory.SECURITY,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP)
    ),
    NEW_DEVICE_LOGIN(
            "New Device Login Alert",
            NotificationCategory.SECURITY,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    PASSWORD_CHANGED(
            "Password Changed",
            NotificationCategory.SECURITY,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    PASSWORD_RESET_REQUESTED(
            "Password Reset Link",
            NotificationCategory.SECURITY,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS)
    ),
    MFA_ENABLED(
            "MFA Enabled",
            NotificationCategory.SECURITY,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP)
    ),
    MFA_DISABLED(
            "MFA Disabled",
            NotificationCategory.SECURITY,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP)
    ),
    OTP_SENT(
            "OTP Verification Code",
            NotificationCategory.SECURITY,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP)
    ),
    ACCOUNT_LOCKED(
            "Account Locked",
            NotificationCategory.SECURITY,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.IN_APP)
    ),

    // ═══════════════════════════════════════════════════════════════════════════
    // SHOPPING EVENTS
    // ═══════════════════════════════════════════════════════════════════════════

    CART_ITEM_ADDED(
            "Item Added to Cart",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.IN_APP)
    ),
    WISHLIST_UPDATED(
            "Wishlist Updated",
            NotificationCategory.PRODUCT_ALERTS,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.IN_APP)
    ),
    PRICE_DROP(
            "Price Drop Alert",
            NotificationCategory.PRODUCT_ALERTS,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    BACK_IN_STOCK(
            "Back in Stock Alert",
            NotificationCategory.PRODUCT_ALERTS,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    ABANDONED_CART(
            "Abandoned Cart Reminder",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    WISHLIST_REMINDER(
            "Wishlist Reminder",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),

    // ═══════════════════════════════════════════════════════════════════════════
    // ORDER EVENTS
    // ═══════════════════════════════════════════════════════════════════════════

    ORDER_PLACED(
            "Order Placed",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    PAYMENT_SUCCESSFUL(
            "Payment Successful",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    PAYMENT_FAILED(
            "Payment Failed",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    PAYMENT_REFUNDED(
            "Payment Refunded",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    ORDER_PROCESSING(
            "Order Processing",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP)
    ),
    ORDER_PACKED(
            "Order Packed",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP)
    ),
    ORDER_SHIPPED(
            "Order Shipped",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    OUT_FOR_DELIVERY(
            "Out for Delivery",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.SMS, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    ORDER_DELIVERED(
            "Order Delivered",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    ORDER_CANCELLED(
            "Order Cancelled",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),

    // ═══════════════════════════════════════════════════════════════════════════
    // RETURN & REFUND EVENTS
    // ═══════════════════════════════════════════════════════════════════════════

    RETURN_REQUESTED(
            "Return Requested",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    RETURN_APPROVED(
            "Return Approved",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    RETURN_REJECTED(
            "Return Rejected",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    PICKUP_SCHEDULED(
            "Return Pickup Scheduled",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    REFUND_INITIATED(
            "Refund Initiated",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    REFUND_COMPLETED(
            "Refund Completed",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),

    // ═══════════════════════════════════════════════════════════════════════════
    // PROMOTIONAL EVENTS
    // ═══════════════════════════════════════════════════════════════════════════

    BIRTHDAY_WISH(
            "Birthday Greeting",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    ANNIVERSARY_WISH(
            "Anniversary Greeting",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    COUPON_ISSUED(
            "Coupon Issued",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    FLASH_SALE(
            "Flash Sale Alert",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    FESTIVAL_OFFER(
            "Festival Offer",
            NotificationCategory.MARKETING,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    LOYALTY_REWARD(
            "Loyalty Reward",
            NotificationCategory.MARKETING,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    GIFT_CARD_ISSUED(
            "Gift Card Issued",
            NotificationCategory.TRANSACTIONAL,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMINISTRATIVE EVENTS (recipient: admin staff, not customers)
    // ═══════════════════════════════════════════════════════════════════════════

    ADMIN_PRODUCT_CREATED(
            "Product Created (Admin)",
            NotificationCategory.ADMIN,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP)
    ),
    INVENTORY_LOW(
            "Inventory Low Alert",
            NotificationCategory.ADMIN,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP,
                    NotificationChannel.IN_APP)
    ),
    INVENTORY_OUT_OF_STOCK(
            "Inventory Out of Stock",
            NotificationCategory.ADMIN,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    DAILY_SALES_REPORT(
            "Daily Sales Report",
            NotificationCategory.ADMIN,
            NotificationPriority.LOW,
            EnumSet.of(NotificationChannel.EMAIL)
    ),
    HIGH_VALUE_ORDER(
            "High Value Order Alert",
            NotificationCategory.ADMIN,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    SUSPICIOUS_LOGIN(
            "Suspicious Login Alert (Admin)",
            NotificationCategory.ADMIN,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP, NotificationChannel.IN_APP)
    ),
    ROLE_CHANGED(
            "User Role Changed",
            NotificationCategory.ADMIN,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP)
    ),
    PERMISSION_CHANGED(
            "User Permission Changed",
            NotificationCategory.ADMIN,
            NotificationPriority.HIGH,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP)
    ),
    CRITICAL_ERROR(
            "Critical System Error",
            NotificationCategory.ADMIN,
            NotificationPriority.CRITICAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.SMS,
                    NotificationChannel.WHATSAPP)
    ),
    SYSTEM_MAINTENANCE(
            "System Maintenance Notice",
            NotificationCategory.SYSTEM,
            NotificationPriority.NORMAL,
            EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP)
    );

    /** Human-readable display name. */
    private final String displayName;

    /** Preference category for user-level control. */
    private final NotificationCategory category;

    /** Default priority level. */
    private final NotificationPriority defaultPriority;

    /** Default delivery channels. Can be overridden by user preferences. */
    private final Set<NotificationChannel> defaultChannels;

    /**
     * Returns the Thymeleaf template key for a given channel.
     * Pattern: {@code {event_type_lowercase}/{channel_lowercase}}
     * Example: {@code user_registered/email}
     */
    public String templateKey(NotificationChannel channel) {
        return name().toLowerCase() + "/" + channel.name().toLowerCase();
    }
}
