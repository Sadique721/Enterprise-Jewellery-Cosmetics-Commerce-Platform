package com.antigravity.sanab.notification.application.port;

import com.antigravity.sanab.notification.domain.entity.Notification;

/**
 * Port (interface) defining the contract for all notification delivery channels.
 *
 * <p>Follows the Ports & Adapters (Hexagonal) pattern. Business logic
 * depends only on this interface, never on concrete provider implementations.
 * This guarantees that providers can be swapped, mocked for testing, or
 * run behind feature flags without touching service code.
 *
 * <p>Each channel (Email, SMS, WhatsApp, In-App) provides exactly one
 * implementation of this interface.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface NotificationChannelPort {

    /**
     * Sends a fully-rendered notification through this channel.
     *
     * <p>Implementations are responsible for:
     * <ul>
     *   <li>Invoking the appropriate external provider</li>
     *   <li>Updating the notification entity with provider response</li>
     *   <li>Throwing a {@link NotificationDeliveryException} on failure
     *       (do NOT swallow exceptions — let Spring Retry handle them)</li>
     * </ul>
     *
     * @param notification the fully-populated, ready-to-send notification entity
     * @throws NotificationDeliveryException if sending fails (triggers retry)
     */
    void send(Notification notification);

    /**
     * Confirms whether this port can handle the given notification.
     *
     * <p>Used for channel routing validation. Should return false only if
     * a channel is disabled or misconfigured at the system level.
     */
    boolean supports(Notification notification);
}
