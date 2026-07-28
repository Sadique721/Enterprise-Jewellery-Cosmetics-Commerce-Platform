package com.antigravity.sanab.notification.event;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all SANAB notification domain events.
 *
 * <p>All business events that trigger notifications implement this interface.
 * Events are immutable value objects (Java records) published via
 * Spring's {@code ApplicationEventPublisher}.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface SanabNotificationEvent {

    /** The type of notification event, used for routing and template resolution. */
    NotificationEventType eventType();

    /** Unique event ID for idempotency tracking. Default generates random UUID. */
    default UUID eventId() {
        return UUID.randomUUID();
    }

    /** Target user ID receiving the notification. Override in event record if present. */
    default UUID userId() {
        return null;
    }

    /** Timestamp when the event was published. Default returns Instant.now(). */
    default Instant timestamp() {
        return Instant.now();
    }

    /** Alias for timestamp() used by event listeners. */
    default Instant occurredAt() {
        return timestamp();
    }

    /** Optional recipient email address. Override in event record if present. */
    default String email() {
        return null;
    }

    /** Optional recipient phone number. Override in event record if present. */
    default String phone() {
        return null;
    }
}
