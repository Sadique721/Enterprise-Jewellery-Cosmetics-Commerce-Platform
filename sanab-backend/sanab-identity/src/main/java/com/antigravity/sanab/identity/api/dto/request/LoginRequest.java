package com.antigravity.sanab.identity.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Login request DTO.
 *
 * @param email    the user's email address
 * @param password the user's plaintext password
 * @param deviceId optional client-generated device fingerprint
 */
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        String deviceId,

        String deviceName
) {}
