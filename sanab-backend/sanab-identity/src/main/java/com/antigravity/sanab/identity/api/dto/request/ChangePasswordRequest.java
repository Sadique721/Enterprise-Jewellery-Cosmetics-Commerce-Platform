package com.antigravity.sanab.identity.api.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request to change password for an authenticated user.
 *
 * @param currentPassword current plaintext password (for re-verification)
 * @param newPassword     new plaintext password
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain uppercase, lowercase, digit, and special character"
        )
        String newPassword
) {}
