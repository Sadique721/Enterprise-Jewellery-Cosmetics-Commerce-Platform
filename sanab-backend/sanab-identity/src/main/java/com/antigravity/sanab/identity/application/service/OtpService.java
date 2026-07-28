package com.antigravity.sanab.identity.application.service;

import java.util.UUID;

/**
 * OTP service port — manages generation, delivery, and verification
 * of one-time passwords across all identity flows.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface OtpService {

    void sendEmailVerificationOtp(UUID userId, String email);

    void sendPasswordResetOtp(UUID userId, String email);

    void sendPhoneVerificationOtp(UUID userId, String phone);

    void sendLoginMfaOtp(UUID userId, String destination, String channel);

    void verifyEmailOtp(UUID userId, String otp);

    void verifyPasswordResetOtp(UUID userId, String otp);

    void verifyPhoneOtp(UUID userId, String otp);

    boolean verifyMfaOtp(UUID userId, String otp);
}
