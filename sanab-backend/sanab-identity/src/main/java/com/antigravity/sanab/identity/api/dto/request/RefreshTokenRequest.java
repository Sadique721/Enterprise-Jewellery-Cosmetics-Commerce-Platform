package com.antigravity.sanab.identity.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to refresh access and refresh tokens using a valid refresh token.
 *
 * @param refreshToken the current refresh token
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
