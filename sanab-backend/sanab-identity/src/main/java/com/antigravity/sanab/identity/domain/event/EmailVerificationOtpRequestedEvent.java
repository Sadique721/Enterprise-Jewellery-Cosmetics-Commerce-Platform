package com.antigravity.sanab.identity.domain.event;

import java.util.UUID;

/** Published when a user requests email verification. */
public record EmailVerificationOtpRequestedEvent(UUID userId, String email, String otp) {}
