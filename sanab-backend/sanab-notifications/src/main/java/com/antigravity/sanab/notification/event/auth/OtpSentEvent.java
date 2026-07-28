package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when an OTP is generated for any verification flow.
 *
 * <p>The channel determines how the OTP is delivered (Email, SMS, or WhatsApp).
 * Priority: CRITICAL — delivered immediately.
 *
 * @param userId         the user the OTP was generated for
 * @param email          user's email (for EMAIL channel)
 * @param phone          user's phone in E.164 format (for SMS/WHATSAPP)
 * @param firstName      user's first name
 * @param otpCode        the 6-digit OTP (never logged)
 * @param channel        the channel to deliver the OTP on
 * @param expiryMinutes  how many minutes the OTP is valid
 * @param occurredAt     event creation timestamp
 */
public record OtpSentEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        String otpCode,
        NotificationChannel channel,
        int expiryMinutes,
        Instant occurredAt
) implements SanabNotificationEvent {

    public OtpSentEvent(UUID userId, String email, String phone, String firstName,
                        String otpCode, NotificationChannel channel, int expiryMinutes) {
        this(userId, email, phone, firstName, otpCode, channel, expiryMinutes, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.OTP_SENT;
    }
}
