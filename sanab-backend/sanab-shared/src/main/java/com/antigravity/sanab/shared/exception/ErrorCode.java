package com.antigravity.sanab.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of all machine-readable error codes used across the SANAB platform.
 *
 * <p>Error codes are grouped by domain module for easy discoverability.
 * All codes follow the convention: {@code DOMAIN_SPECIFIC_ERROR}.
 *
 * <p>These codes are included in every error {@link com.antigravity.sanab.shared.api.response.ApiError}
 * response, enabling API consumers to implement precise error handling.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ─── Generic ──────────────────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("An internal server error occurred"),
    VALIDATION_FAILED("Request validation failed"),
    RESOURCE_NOT_FOUND("The requested resource was not found"),
    RESOURCE_ALREADY_EXISTS("The resource already exists"),
    ACCESS_DENIED("Access denied"),
    UNAUTHORIZED("Authentication required"),
    METHOD_NOT_ALLOWED("HTTP method not allowed"),
    UNSUPPORTED_MEDIA_TYPE("Unsupported media type"),
    BAD_REQUEST("Bad request"),
    SERVICE_UNAVAILABLE("Service temporarily unavailable"),
    OPTIMISTIC_LOCK_CONFLICT("Resource was modified by another operation. Please retry."),

    // ─── Identity / Authentication ────────────────────────────────────────────
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("A user with this email already exists"),
    EMAIL_ALREADY_EXISTS("An account with this email already exists"),
    PHONE_ALREADY_EXISTS("An account with this phone number already exists"),
    INVALID_CREDENTIALS("Invalid email or password"),
    ACCOUNT_LOCKED("Account is locked due to too many failed attempts"),
    ACCOUNT_DISABLED("Account has been disabled"),
    ACCOUNT_SUSPENDED("Account has been suspended. Contact support."),
    EMAIL_NOT_VERIFIED("Email address must be verified before login"),
    PHONE_NOT_VERIFIED("Phone number must be verified"),
    INVALID_TOKEN("Token is invalid or has expired"),
    TOKEN_EXPIRED("Token has expired"),
    TOKEN_REVOKED("Token has been revoked"),
    TOKEN_REUSE_DETECTED("Security violation: token reuse detected. All sessions revoked."),
    REFRESH_TOKEN_INVALID("Refresh token is invalid or expired"),
    SESSION_NOT_FOUND("Session not found"),
    DEVICE_NOT_TRUSTED("Login from new device requires verification"),
    PASSWORD_MISMATCH("Passwords do not match"),
    PASSWORD_TOO_WEAK("Password does not meet security requirements"),
    PASSWORD_RECENTLY_USED("This password was recently used. Please choose a different one."),
    MFA_REQUIRED("Multi-factor authentication is required"),
    MFA_INVALID_CODE("Invalid MFA code"),
    MFA_ALREADY_ENABLED("MFA is already enabled for this account"),
    MFA_NOT_ENABLED("MFA is not enabled for this account"),
    CAPTCHA_REQUIRED("CAPTCHA verification required"),
    CAPTCHA_INVALID("CAPTCHA verification failed"),
    OTP_INVALID("OTP is invalid or has expired"),
    OTP_EXPIRED("OTP has expired. Please request a new one."),
    OTP_MAX_ATTEMPTS("Maximum OTP attempts exceeded"),
    RATE_LIMIT_EXCEEDED("Too many requests. Please slow down."),

    // ─── Catalog ──────────────────────────────────────────────────────────────
    PRODUCT_NOT_FOUND("Product not found"),
    PRODUCT_ALREADY_EXISTS("Product already exists"),
    PRODUCT_VARIANT_NOT_FOUND("Product variant not found"),
    CATEGORY_NOT_FOUND("Category not found"),
    BRAND_NOT_FOUND("Brand not found"),
    PRODUCT_SLUG_CONFLICT("A product with this slug already exists"),
    CATEGORY_HIERARCHY_EXCEEDED("Category depth cannot exceed 3 levels"),

    // ─── Inventory ────────────────────────────────────────────────────────────
    INSUFFICIENT_STOCK("Insufficient stock for the requested quantity"),
    PRODUCT_OUT_OF_STOCK("Product is currently out of stock"),

    // ─── Cart ─────────────────────────────────────────────────────────────────
    CART_NOT_FOUND("Cart not found"),
    CART_ITEM_NOT_FOUND("Cart item not found"),
    CART_EMPTY("Cart is empty"),

    // ─── Orders ───────────────────────────────────────────────────────────────
    ORDER_NOT_FOUND("Order not found"),
    ORDER_CANNOT_BE_CANCELLED("Order cannot be cancelled in its current state"),
    ORDER_STATE_TRANSITION_INVALID("Invalid order state transition"),

    // ─── Payments ─────────────────────────────────────────────────────────────
    PAYMENT_FAILED("Payment processing failed"),
    PAYMENT_NOT_FOUND("Payment not found"),
    REFUND_FAILED("Refund processing failed"),
    PAYMENT_ALREADY_PROCESSED("Payment has already been processed"),

    // ─── Shipping ─────────────────────────────────────────────────────────────
    SHIPMENT_NOT_FOUND("Shipment not found"),

    // ─── Customer ─────────────────────────────────────────────────────────────
    ADDRESS_NOT_FOUND("Address not found"),
    ADDRESS_LIMIT_EXCEEDED("Maximum number of addresses reached"),
    WISHLIST_ITEM_NOT_FOUND("Wishlist item not found"),

    // ─── Promotions ───────────────────────────────────────────────────────────
    COUPON_NOT_FOUND("Coupon not found"),
    COUPON_EXPIRED("Coupon has expired"),
    COUPON_USAGE_LIMIT_REACHED("Coupon usage limit has been reached"),
    COUPON_ALREADY_USED("You have already used this coupon"),
    GIFT_CARD_NOT_FOUND("Gift card not found"),
    GIFT_CARD_INSUFFICIENT_BALANCE("Gift card has insufficient balance"),
    LOYALTY_INSUFFICIENT_POINTS("Insufficient loyalty points"),

    // ─── Notifications ────────────────────────────────────────────────────────
    NOTIFICATION_NOT_FOUND("Notification not found"),
    TEMPLATE_NOT_FOUND("Notification template not found"),
    NOTIFICATION_SEND_FAILED("Failed to send notification"),
    NOTIFICATION_TEMPLATE_NOT_FOUND("Notification template not found"),
    NOTIFICATION_PREFERENCE_NOT_FOUND("Notification preferences not found"),

    // ─── CMS ──────────────────────────────────────────────────────────────────
    BLOG_POST_NOT_FOUND("Blog post not found"),
    BANNER_NOT_FOUND("Banner not found"),
    FAQ_NOT_FOUND("FAQ not found"),

    // ─── Support ──────────────────────────────────────────────────────────────
    TICKET_NOT_FOUND("Support ticket not found"),

    // ─── File Upload ──────────────────────────────────────────────────────────
    FILE_UPLOAD_FAILED("File upload failed"),
    FILE_TYPE_NOT_ALLOWED("File type is not allowed"),
    FILE_SIZE_EXCEEDED("File size exceeds the maximum allowed limit"),
    INVALID_MIME_TYPE("Invalid MIME type");

    /** Human-readable default message for this error code. */
    private final String defaultMessage;
}
