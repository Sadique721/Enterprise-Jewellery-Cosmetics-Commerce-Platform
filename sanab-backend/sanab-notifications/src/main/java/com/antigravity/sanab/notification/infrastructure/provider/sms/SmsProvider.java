package com.antigravity.sanab.notification.infrastructure.provider.sms;

import com.antigravity.sanab.notification.domain.enums.ProviderType;

/**
 * SPI for SMS delivery providers.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface SmsProvider {

    /**
     * Sends an SMS message.
     *
     * @param to      recipient phone number in E.164 format (e.g. "+919876543210")
     * @param message the SMS body (≤ 160 chars for single-part)
     * @return the provider-assigned message SID for tracking
     * @throws com.antigravity.sanab.notification.application.port.NotificationDeliveryException on failure
     */
    String sendSms(String to, String message);

    /** Returns this provider's identifier. */
    ProviderType providerType();
}
