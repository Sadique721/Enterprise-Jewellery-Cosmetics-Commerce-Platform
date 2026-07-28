package com.antigravity.sanab.notification.infrastructure.provider.email;

import com.antigravity.sanab.notification.domain.enums.ProviderType;

/**
 * SPI (Service Provider Interface) for email delivery providers.
 *
 * <p>Multiple providers may be registered. The active provider
 * is selected via {@code sanab.notification.email.provider} configuration.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface EmailProvider {

    /**
     * Sends an HTML email.
     *
     * @param to          recipient email address
     * @param subject     email subject line
     * @param htmlBody    rendered HTML body
     * @param recipientName recipient's display name (for personalization header)
     * @return the provider-assigned message ID for tracking
     * @throws com.antigravity.sanab.notification.application.port.NotificationDeliveryException on failure
     */
    String sendHtml(String to, String subject, String htmlBody, String recipientName);

    /**
     * Sends an HTML email with a PDF attachment.
     *
     * @param to             recipient email address
     * @param subject        email subject line
     * @param htmlBody       rendered HTML body
     * @param recipientName  recipient's display name
     * @param attachmentName filename for the attachment (e.g. "invoice.pdf")
     * @param attachmentData raw PDF bytes
     * @return the provider-assigned message ID
     */
    String sendHtmlWithAttachment(String to, String subject, String htmlBody,
                                   String recipientName, String attachmentName,
                                   byte[] attachmentData);

    /** Returns this provider's identifier. */
    ProviderType providerType();
}
