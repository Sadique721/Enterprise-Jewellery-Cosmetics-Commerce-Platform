package com.antigravity.sanab.notification.api.dto.request;

/**
 * Request DTO for updating user notification preferences.
 *
 * <p>All fields are optional (null = no change).
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public record UpdatePreferenceRequest(
        // Channel preferences (null = no change)
        Boolean emailEnabled,
        Boolean smsEnabled,
        Boolean whatsAppEnabled,
        Boolean pushEnabled,
        Boolean inAppEnabled,
        // Category preferences (null = no change)
        Boolean transactionalNotificationsEnabled,
        Boolean marketingEnabled,
        Boolean productAlertsEnabled,
        Boolean systemNotificationsEnabled,
        Boolean newsletterEnabled
        // Note: securityNotificationsEnabled is intentionally excluded
        // as it cannot be disabled via API.
) {}
