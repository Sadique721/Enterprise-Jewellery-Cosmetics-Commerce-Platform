package com.antigravity.sanab.identity.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request to verify a user's email address with a one-time OTP.
 *
 * @param userId the user's UUID
 * @param otp    the verification OTP sent to the user's email
 */
public record VerifyEmailRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "OTP is required")
        String otp
) {}
