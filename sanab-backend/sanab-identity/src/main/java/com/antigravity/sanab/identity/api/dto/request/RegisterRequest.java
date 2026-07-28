package com.antigravity.sanab.identity.api.dto.request;

import jakarta.validation.constraints.*;

/**
 * Registration request DTO.
 *
 * @param firstName user's first name
 * @param lastName  user's last name
 * @param email     unique email address
 * @param phone     optional phone number (E.164 format)
 * @param password  plaintext password — hashed server-side, never stored
 */
public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @Pattern(
            regexp = "^\\+[1-9]\\d{6,14}$",
            message = "Phone must be in E.164 format (e.g., +919876543210)"
        )
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase, lowercase, digit, and special character"
        )
        String password
) {}
