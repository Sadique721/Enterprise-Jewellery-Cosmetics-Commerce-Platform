package com.antigravity.sanab.identity.application.service;

import com.antigravity.sanab.identity.api.dto.request.LoginRequest;
import com.antigravity.sanab.identity.api.dto.request.RegisterRequest;
import com.antigravity.sanab.identity.api.dto.response.AuthResponse;

import java.util.UUID;

/**
 * Authentication service port — defines all auth business operations.
 *
 * <p>Implementations must never expose internal state or return raw entities.
 * All operations return DTOs or throw {@link com.antigravity.sanab.shared.exception.SanabException}.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * Registers a new user account and dispatches a verification email.
     *
     * @param request registration payload
     * @param ip      client IP address for audit logging
     * @return a partial auth response (tokens not issued until email is verified)
     */
    AuthResponse register(RegisterRequest request, String ip);

    /**
     * Authenticates a user with email and password.
     *
     * <p>Issues access + refresh tokens on success. Returns an MFA challenge
     * indicator if MFA is enabled for the account.
     *
     * @param request   login credentials
     * @param ip        client IP for brute-force tracking
     * @param userAgent request User-Agent for session fingerprinting
     * @return authentication tokens and user info
     */
    AuthResponse login(LoginRequest request, String ip, String userAgent);

    /**
     * Rotates a refresh token — returns a new token pair.
     *
     * <p>Detects token reuse (replay attacks) and revokes all sessions
     * for the user family if a reused token is detected.
     *
     * @param refreshToken the current refresh token
     * @param ip           client IP
     * @return new token pair
     */
    AuthResponse refreshTokens(String refreshToken, String ip);

    /**
     * Logs out the current session by revoking the refresh token
     * and blacklisting the access token.
     *
     * @param accessToken  the current access token (to blacklist)
     * @param refreshToken the current refresh token (to revoke)
     */
    void logout(String accessToken, String refreshToken);

    /**
     * Logs out all active sessions for a user.
     *
     * @param userId the user's UUID
     */
    void logoutAll(UUID userId);

    /**
     * Verifies a user's email using a time-limited OTP token.
     *
     * @param userId the user's UUID
     * @param otp    the verification OTP
     */
    void verifyEmail(UUID userId, String otp);

    /**
     * Initiates the password reset flow by sending a reset OTP.
     *
     * @param email the user's email address
     */
    void initiatePasswordReset(String email);

    /**
     * Completes the password reset flow with a new password.
     *
     * @param email       the user's email
     * @param otp         the reset OTP
     * @param newPassword the new plaintext password
     */
    void resetPassword(String email, String otp, String newPassword);

    /**
     * Changes the password for an authenticated user.
     *
     * @param userId          the authenticated user's UUID
     * @param currentPassword the current plaintext password (for re-verification)
     * @param newPassword     the new plaintext password
     */
    void changePassword(UUID userId, String currentPassword, String newPassword);
}
