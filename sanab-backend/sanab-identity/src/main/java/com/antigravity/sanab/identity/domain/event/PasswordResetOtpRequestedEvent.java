package com.antigravity.sanab.identity.domain.event;

import java.util.UUID;

/** Published when a user requests a password reset OTP. */
public record PasswordResetOtpRequestedEvent(UUID userId, String email, String otp) {}
