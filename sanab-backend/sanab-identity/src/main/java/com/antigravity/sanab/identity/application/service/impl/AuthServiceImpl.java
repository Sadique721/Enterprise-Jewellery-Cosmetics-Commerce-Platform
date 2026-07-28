package com.antigravity.sanab.identity.application.service.impl;

import com.antigravity.sanab.identity.api.dto.request.LoginRequest;
import com.antigravity.sanab.identity.api.dto.request.RegisterRequest;
import com.antigravity.sanab.identity.api.dto.response.AuthResponse;
import com.antigravity.sanab.identity.application.service.AuthService;
import com.antigravity.sanab.identity.application.service.OtpService;
import com.antigravity.sanab.identity.domain.entity.User;
import com.antigravity.sanab.identity.domain.entity.UserSession;
import com.antigravity.sanab.identity.domain.entity.UserStatus;
import com.antigravity.sanab.identity.domain.repository.UserRepository;
import com.antigravity.sanab.identity.domain.repository.UserSessionRepository;
import com.antigravity.sanab.security.jwt.JwtBlacklist;
import com.antigravity.sanab.security.jwt.JwtTokenProvider;
import com.antigravity.sanab.security.config.JwtProperties;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AuthService implementation — orchestrates the full authentication lifecycle.
 *
 * <p>Security guarantees provided by this implementation:
 * <ul>
 *   <li>Passwords hashed with Argon2id (never stored in plaintext)</li>
 *   <li>Constant-time password comparison (prevents timing attacks)</li>
 *   <li>Refresh tokens stored as SHA-256 hashes only</li>
 *   <li>Token family revocation on refresh token replay detection</li>
 *   <li>Brute-force protection via failed login counter + lockout</li>
 *   <li>Audit logging of all login attempts, successes, and failures</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider      jwtTokenProvider;
    private final JwtBlacklist          jwtBlacklist;
    private final JwtProperties         jwtProperties;
    private final PasswordEncoder       passwordEncoder;
    private final OtpService            otpService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    // ─── Registration ─────────────────────────────────────────────────────────

    @Override
    public AuthResponse register(RegisterRequest req, String ip) {
        // 1. Check for duplicate email
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new SanabException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "An account with this email already exists");
        }

        // 2. Check for duplicate phone (if provided)
        if (req.phone() != null && userRepository.existsByPhone(req.phone())) {
            throw new SanabException(ErrorCode.PHONE_ALREADY_EXISTS,
                    "An account with this phone number already exists");
        }

        // 3. Hash password with Argon2id
        String passwordHash = passwordEncoder.encode(req.password());

        // 4. Create and persist user (Active by default for luxury customer onboarding)
        User user = User.builder()
                .firstName(req.firstName().strip())
                .lastName(req.lastName().strip())
                .email(req.email().toLowerCase().strip())
                .phone(req.phone())
                .passwordHash(passwordHash)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User registered: id={}, email={}, ip={}", user.getId(), user.getEmail(), ip);

        // 5. Send email verification OTP
        try {
            otpService.sendEmailVerificationOtp(user.getId(), user.getEmail());
        } catch (Exception e) {
            log.warn("OTP notice: {}", e.getMessage());
        }

        return AuthResponse.of(null, null,
                0L, user.getId(), user.getEmail(),
                user.getFullName(), Set.of(), true, false);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest req, String ip, String userAgent) {
        // 1. Find user
        User user = userRepository.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> {
                    log.warn("Login failed — user not found: email={}, ip={}", req.email(), ip);
                    // Return generic message to prevent user enumeration
                    return new SanabException(ErrorCode.INVALID_CREDENTIALS,
                            "Invalid email or password");
                });

        // 2. Check account status
        if (user.isAccountLocked()) {
            log.warn("Login blocked — account locked: userId={}, ip={}", user.getId(), ip);
            throw new SanabException(ErrorCode.ACCOUNT_LOCKED,
                    "Account is temporarily locked. Please try again later.");
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new SanabException(ErrorCode.ACCOUNT_SUSPENDED,
                    "Account has been suspended. Contact support.");
        }
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new SanabException(ErrorCode.EMAIL_NOT_VERIFIED,
                    "Please verify your email before logging in.");
        }

        // 3. Verify password (constant-time comparison)
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            user.recordFailedLogin();
            userRepository.save(user);
            log.warn("Login failed — wrong password: userId={}, attempts={}, ip={}",
                    user.getId(), user.getFailedLoginAttempts(), ip);
            throw new SanabException(ErrorCode.INVALID_CREDENTIALS,
                    "Invalid email or password");
        }

        // 4. Record successful login
        user.recordSuccessfulLogin(ip);
        userRepository.save(user);

        // 5. Extract role names
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        // 6. Issue tokens
        String sessionId = UUID.randomUUID().toString();
        String accessToken  = jwtTokenProvider.issueAccessToken(
                user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.issueRefreshToken(
                user.getId(), UUID.fromString(sessionId));

        // 7. Persist session (store hash of refresh token — never plain token)
        UserSession session = UserSession.builder()
                .userId(user.getId())
                .refreshTokenHash(sha256(refreshToken))
                .familyId(sessionId)
                .deviceId(req.deviceId())
                .deviceName(req.deviceName())
                .userAgent(userAgent)
                .ipAddress(ip)
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpiration()))
                .active(true)
                .build();

        sessionRepository.save(session);

        log.info("Login successful: userId={}, ip={}", user.getId(), ip);

        return AuthResponse.of(
                accessToken, refreshToken,
                jwtProperties.getAccessTokenExpiration(),
                user.getId(), user.getEmail(),
                user.getFullName(), roles,
                user.isMfaEnabled(), user.isEmailVerified()
        );
    }

    // ─── Token Refresh ────────────────────────────────────────────────────────

    @Override
    public AuthResponse refreshTokens(String refreshToken, String ip) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new SanabException(ErrorCode.INVALID_TOKEN, "Invalid or expired refresh token");
        }

        String tokenHash = sha256(refreshToken);
        UserSession session = sessionRepository.findByRefreshTokenHashAndActiveTrue(tokenHash)
                .orElseGet(() -> {
                    // Token not found but valid signature — replay attack detected!
                    var claims = jwtTokenProvider.extractRefreshClaims(refreshToken);
                    String familyId = (String) claims.get("sid");
                    if (familyId != null) {
                        log.error("REPLAY ATTACK DETECTED — revoking all family sessions: family={}",
                                familyId);
                        sessionRepository.revokeFamilySessions(familyId);
                    }
                    throw new SanabException(ErrorCode.TOKEN_REUSE_DETECTED,
                            "Security violation detected. Please log in again.");
                });

        if (session.isExpired()) {
            session.revoke();
            sessionRepository.save(session);
            throw new SanabException(ErrorCode.TOKEN_EXPIRED, "Session has expired");
        }

        UUID userId = session.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SanabException(ErrorCode.USER_NOT_FOUND,
                        "User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());

        // Rotate tokens
        String newSessionId   = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenProvider.issueAccessToken(
                userId, user.getEmail(), roles);
        String newRefreshToken = jwtTokenProvider.issueRefreshToken(
                userId, UUID.fromString(newSessionId));

        // Revoke old session, create new one (token rotation)
        session.revoke();
        sessionRepository.save(session);

        UserSession newSession = UserSession.builder()
                .userId(userId)
                .refreshTokenHash(sha256(newRefreshToken))
                .familyId(newSessionId)
                .deviceId(session.getDeviceId())
                .deviceName(session.getDeviceName())
                .userAgent(session.getUserAgent())
                .ipAddress(ip)
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpiration()))
                .active(true)
                .build();

        sessionRepository.save(newSession);

        return AuthResponse.of(
                newAccessToken, newRefreshToken,
                jwtProperties.getAccessTokenExpiration(),
                userId, user.getEmail(),
                user.getFullName(), roles,
                user.isMfaEnabled(), user.isEmailVerified()
        );
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    @Override
    public void logout(String accessToken, String refreshToken) {
        // Blacklist access token (prevents use until expiry)
        if (jwtTokenProvider.validateAccessToken(accessToken)) {
            String jti    = jwtTokenProvider.extractJti(accessToken);
            Instant expiry = jwtTokenProvider.extractExpiry(accessToken);
            jwtBlacklist.blacklist(jti, expiry);
        }

        // Revoke refresh token session
        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String tokenHash = sha256(refreshToken);
            sessionRepository.findByRefreshTokenHashAndActiveTrue(tokenHash)
                    .ifPresent(s -> {
                        s.revoke();
                        sessionRepository.save(s);
                    });
        }

        log.info("User logged out successfully");
    }

    @Override
    public void logoutAll(UUID userId) {
        sessionRepository.revokeAllSessions(userId);
        log.info("All sessions revoked for userId={}", userId);
    }

    // ─── Email Verification ───────────────────────────────────────────────────

    @Override
    public void verifyEmail(UUID userId, String otp) {
        otpService.verifyEmailOtp(userId, otp);
        userRepository.markEmailVerified(userId);
        log.info("Email verified: userId={}", userId);
    }

    // ─── Password Reset ───────────────────────────────────────────────────────

    @Override
    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email.toLowerCase())
                .ifPresent(user -> {
                    // Only send if user is active (don't reveal existence for non-existent emails)
                    if (user.getStatus() == UserStatus.ACTIVE) {
                        otpService.sendPasswordResetOtp(user.getId(), user.getEmail());
                    }
                });
        // Always respond with the same message regardless of email existence
        log.info("Password reset initiated for: email={}", email);
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new SanabException(ErrorCode.USER_NOT_FOUND,
                        "No account found with this email"));

        otpService.verifyPasswordResetOtp(user.getId(), otp);

        String newHash = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(user.getId(), newHash, Instant.now());

        // Revoke all sessions after password reset
        sessionRepository.revokeAllSessions(user.getId());

        log.info("Password reset completed: userId={}", user.getId());
    }

    @Override
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SanabException(ErrorCode.USER_NOT_FOUND,
                        "User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new SanabException(ErrorCode.INVALID_CREDENTIALS,
                    "Current password is incorrect");
        }

        String newHash = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(userId, newHash, Instant.now());

        // Revoke all other sessions after password change
        sessionRepository.revokeAllSessions(userId);

        log.info("Password changed: userId={}", userId);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
