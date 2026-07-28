package com.antigravity.sanab.identity.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * User session entity — tracks active login sessions per device.
 *
 * <p>Each refresh token corresponds to exactly one session. When a refresh
 * token is used, the old session's token hash is replaced with the new one
 * (token rotation). If a reused (old) refresh token is detected, all sessions
 * for that user are invalidated (replay attack prevention).
 *
 * <p>Schema: {@code identity.user_sessions}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "user_sessions",
    schema = "identity",
    indexes = {
        @Index(name = "idx_sessions_user_id",       columnList = "user_id"),
        @Index(name = "idx_sessions_token_hash",     columnList = "refresh_token_hash"),
        @Index(name = "idx_sessions_device_id",      columnList = "device_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private java.util.UUID userId;

    /** SHA-256 hash of the refresh token (never store plain tokens). */
    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    /** Session family ID — used to detect refresh token replay attacks. */
    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    /** Device fingerprint (user-agent + OS + browser). */
    @Column(name = "device_id", length = 200)
    private String deviceId;

    @Column(name = "device_name", length = 150)
    private String deviceName;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /** Marks whether this session was established after MFA verification. */
    @Column(name = "mfa_verified", nullable = false)
    @Builder.Default
    private boolean mfaVerified = false;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void revoke() {
        this.active = false;
    }
}
