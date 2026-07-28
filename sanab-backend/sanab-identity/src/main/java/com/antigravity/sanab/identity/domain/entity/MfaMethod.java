package com.antigravity.sanab.identity.domain.entity;

/**
 * Supported MFA methods for SANAB user accounts.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public enum MfaMethod {

    /** Time-based One-Time Password (Google Authenticator, Authy). */
    TOTP,

    /** OTP sent via email. */
    EMAIL_OTP,

    /** OTP sent via SMS. */
    SMS_OTP,

    /** OTP sent via WhatsApp. */
    WHATSAPP_OTP
}
