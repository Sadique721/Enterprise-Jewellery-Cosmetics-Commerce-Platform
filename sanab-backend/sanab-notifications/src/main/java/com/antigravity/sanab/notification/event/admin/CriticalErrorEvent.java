package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Critical system error alert. Priority: CRITICAL. */
public record CriticalErrorEvent(
        UUID userId, String email, String phone, String firstName,
        String errorType, String errorMessage, String stackTraceSummary,
        String module, Instant occurredAt
) implements SanabNotificationEvent {
    public CriticalErrorEvent(String errorType, String errorMessage,
                              String stackTraceSummary, String module) {
        this(null, null, null, "Admin", errorType, errorMessage,
                stackTraceSummary, module, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.CRITICAL_ERROR; }
}
