package com.antigravity.sanab.identity.application.service.impl;

import com.antigravity.sanab.identity.application.service.OtpService;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

/**
 * Redis-backed OTP service implementation.
 *
 * <p>OTPs are:
 * <ul>
 *   <li>6-digit numeric codes generated with {@link SecureRandom}</li>
 *   <li>Stored in Redis with a 5-minute TTL (auto-expire)</li>
 *   <li>Invalidated immediately after successful verification (single-use)</li>
 *   <li>Rate-limited — max 3 attempts before the OTP is invalidated</li>
 * </ul>
 *
 * <p>OTP delivery is delegated to the {@code sanab-notifications} module
 * via Spring application events (no direct dependency on email/SMS providers).
 *
 * <p>Redis key formats:
 * <pre>
 *   sanab:otp:email_verification:{userId}
 *   sanab:otp:password_reset:{userId}
 *   sanab:otp:phone_verification:{userId}
 *   sanab:otp:mfa:{userId}
 *   sanab:otp:attempts:{type}:{userId}
 * </pre>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int    OTP_LENGTH          = 6;
    private static final long   OTP_TTL_SECONDS     = 300L;   // 5 minutes
    private static final int    MAX_ATTEMPTS        = 3;
    private static final String OTP_KEY_PREFIX      = "sanab:otp:";
    private static final String ATTEMPTS_KEY_PREFIX = "sanab:otp:attempts:";

    private final StringRedisTemplate      redisTemplate;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    private final SecureRandom secureRandom = new SecureRandom();

    // ─── Send ─────────────────────────────────────────────────────────────────

    @Override
    public void sendEmailVerificationOtp(UUID userId, String email) {
        String otp = generateOtp();
        storeOtp("email_verification", userId, otp);
        // Publish notification event — sanab-notifications handles actual delivery
        eventPublisher.publishEvent(
                new com.antigravity.sanab.identity.domain.event.EmailVerificationOtpRequestedEvent(
                        userId, email, otp));
        log.debug("Email verification OTP generated for userId={}", userId);
    }

    @Override
    public void sendPasswordResetOtp(UUID userId, String email) {
        String otp = generateOtp();
        storeOtp("password_reset", userId, otp);
        eventPublisher.publishEvent(
                new com.antigravity.sanab.identity.domain.event.PasswordResetOtpRequestedEvent(
                        userId, email, otp));
        log.debug("Password reset OTP generated for userId={}", userId);
    }

    @Override
    public void sendPhoneVerificationOtp(UUID userId, String phone) {
        String otp = generateOtp();
        storeOtp("phone_verification", userId, otp);
        eventPublisher.publishEvent(
                new com.antigravity.sanab.identity.domain.event.PhoneVerificationOtpRequestedEvent(
                        userId, phone, otp));
        log.debug("Phone verification OTP generated for userId={}", userId);
    }

    @Override
    public void sendLoginMfaOtp(UUID userId, String destination, String channel) {
        String otp = generateOtp();
        storeOtp("mfa", userId, otp);
        eventPublisher.publishEvent(
                new com.antigravity.sanab.identity.domain.event.MfaOtpRequestedEvent(
                        userId, destination, channel, otp));
        log.debug("MFA OTP generated for userId={}", userId);
    }

    // ─── Verify ───────────────────────────────────────────────────────────────

    @Override
    public void verifyEmailOtp(UUID userId, String otp) {
        verifyOtp("email_verification", userId, otp);
    }

    @Override
    public void verifyPasswordResetOtp(UUID userId, String otp) {
        verifyOtp("password_reset", userId, otp);
    }

    @Override
    public void verifyPhoneOtp(UUID userId, String otp) {
        verifyOtp("phone_verification", userId, otp);
    }

    @Override
    public boolean verifyMfaOtp(UUID userId, String otp) {
        try {
            verifyOtp("mfa", userId, otp);
            return true;
        } catch (SanabException e) {
            return false;
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private void verifyOtp(String type, UUID userId, String inputOtp) {
        String otpKey      = OTP_KEY_PREFIX + type + ":" + userId;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + type + ":" + userId;

        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null) {
            throw new SanabException(ErrorCode.OTP_EXPIRED,
                    "OTP has expired. Please request a new one.");
        }

        // Check attempts
        String attemptsStr = redisTemplate.opsForValue().get(attemptsKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            throw new SanabException(ErrorCode.OTP_MAX_ATTEMPTS,
                    "Too many incorrect attempts. Please request a new OTP.");
        }

        if (!storedOtp.equals(inputOtp)) {
            redisTemplate.opsForValue().increment(attemptsKey);
            redisTemplate.expire(attemptsKey, Duration.ofSeconds(OTP_TTL_SECONDS));
            throw new SanabException(ErrorCode.OTP_INVALID, "Incorrect OTP");
        }

        // Valid — delete OTP (single-use)
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptsKey);
        log.debug("OTP verified successfully: type={}, userId={}", type, userId);
    }

    private void storeOtp(String type, UUID userId, String otp) {
        String key = OTP_KEY_PREFIX + type + ":" + userId;
        redisTemplate.opsForValue().set(key, otp, Duration.ofSeconds(OTP_TTL_SECONDS));
    }

    private String generateOtp() {
        int code = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(code);
    }
}
