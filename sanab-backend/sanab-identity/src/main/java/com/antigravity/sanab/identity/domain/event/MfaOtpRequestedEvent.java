package com.antigravity.sanab.identity.domain.event;

import java.util.UUID;

/** Published when a user needs an MFA OTP delivered. */
public record MfaOtpRequestedEvent(UUID userId, String destination, String channel, String otp) {}
