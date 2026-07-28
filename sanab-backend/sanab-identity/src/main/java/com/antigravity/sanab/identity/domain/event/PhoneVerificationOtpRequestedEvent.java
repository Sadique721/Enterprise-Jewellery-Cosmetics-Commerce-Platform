package com.antigravity.sanab.identity.domain.event;

import java.util.UUID;

/** Published when a user requests phone number verification. */
public record PhoneVerificationOtpRequestedEvent(UUID userId, String phone, String otp) {}
