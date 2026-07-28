package com.antigravity.sanab.notification.infrastructure.provider.whatsapp;

import com.antigravity.sanab.notification.domain.enums.ProviderType;

/**
 * SPI for WhatsApp delivery providers.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface WhatsAppProvider {

    /**
     * Sends a text WhatsApp message.
     *
     * @param to      recipient phone number in E.164 format (e.g. "+919876543210")
     * @param message the message body
     * @return the provider-assigned message SID
     * @throws com.antigravity.sanab.notification.application.port.NotificationDeliveryException on failure
     */
    String sendMessage(String to, String message);

    /** Returns this provider's identifier. */
    ProviderType providerType();
}
