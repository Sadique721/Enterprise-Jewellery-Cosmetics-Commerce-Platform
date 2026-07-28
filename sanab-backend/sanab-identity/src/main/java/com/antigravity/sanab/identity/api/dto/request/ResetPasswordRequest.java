package com.antigravity.sanab.identity.api.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request to complete a password reset using a one-time OTP.
 *
 * @param email       the user's email address
 * @param otp         the time-limited OTP sent to the user's email
 * @param newPassword the new plaintext password (validated server-side)
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 8, message = "Invalid OTP format")
        String otp,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain uppercase, lowercase, digit, and special character"
        )
        String newPassword
) {}
