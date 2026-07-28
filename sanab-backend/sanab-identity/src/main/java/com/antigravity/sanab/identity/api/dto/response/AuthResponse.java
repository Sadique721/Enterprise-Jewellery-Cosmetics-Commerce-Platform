package com.antigravity.sanab.identity.api.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Authentication response returned after successful login or token refresh.
 *
 * @param accessToken        signed JWT access token (short-lived)
 * @param refreshToken       signed JWT refresh token (long-lived)
 * @param tokenType          always "Bearer"
 * @param expiresIn          access token TTL in seconds
 * @param userId             the authenticated user's UUID
 * @param email              the authenticated user's email
 * @param fullName           the authenticated user's full name
 * @param roles              set of granted role names
 * @param mfaRequired        true if MFA challenge is required before full access
 * @param emailVerified      whether the user's email is verified
 * @param issuedAt           token issue timestamp
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        String fullName,
        Set<String> roles,
        boolean mfaRequired,
        boolean emailVerified,
        Instant issuedAt
) {
    /** Convenience factory for the common case. */
    public static AuthResponse of(
            String accessToken,
            String refreshToken,
            long expiresIn,
            UUID userId,
            String email,
            String fullName,
            Set<String> roles,
            boolean mfaRequired,
            boolean emailVerified) {
        return new AuthResponse(
                accessToken, refreshToken, "Bearer", expiresIn,
                userId, email, fullName, roles,
                mfaRequired, emailVerified, Instant.now()
        );
    }
}
