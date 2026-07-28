package com.antigravity.sanab.identity.domain.entity;

/**
 * Account lifecycle status for a SANAB user.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public enum UserStatus {

    /** Registered but email not yet verified. */
    PENDING_VERIFICATION,

    /** Active and able to authenticate. */
    ACTIVE,

    /** Temporarily locked due to brute-force detection (auto-unlocks). */
    TEMPORARILY_LOCKED,

    /** Permanently locked — requires admin review. */
    LOCKED,

    /** Account suspended by admin (e.g., fraud investigation). */
    SUSPENDED,

    /** User has deactivated their own account. */
    DEACTIVATED,

    /** Account deleted (soft-delete — data retained for legal compliance). */
    DELETED
}
