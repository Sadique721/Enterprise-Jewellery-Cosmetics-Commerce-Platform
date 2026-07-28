package com.antigravity.sanab.identity.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Core user account entity for the SANAB platform.
 *
 * <p>Stores identity data only — no profile/preference data (that lives in
 * {@code sanab-customer}). The identity module handles authentication concerns.
 *
 * <p>Schema: {@code identity.users}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "users",
    schema = "identity",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_email",  columnNames = "email"),
        @UniqueConstraint(name = "uq_users_phone",  columnNames = "phone")
    },
    indexes = {
        @Index(name = "idx_users_email",  columnList = "email"),
        @Index(name = "idx_users_phone",  columnList = "phone"),
        @Index(name = "idx_users_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    // ─── Account Status ───────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean phoneVerified = false;

    // ─── MFA ─────────────────────────────────────────────────────────────────

    @Column(nullable = false)
    @Builder.Default
    private boolean mfaEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MfaMethod mfaMethod;

    /** Encrypted TOTP secret — only set when mfaMethod = TOTP. */
    @Column(length = 500)
    private String totpSecret;

    // ─── Security ─────────────────────────────────────────────────────────────

    /** Count of consecutive failed login attempts (reset on success). */
    @Column(nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    /** When the account was locked (null if not locked). */
    private Instant lockedUntil;

    /** When the user last logged in. */
    private Instant lastLoginAt;

    /** IP address of last successful login. */
    @Column(length = 50)
    private String lastLoginIp;

    /** When the user last changed their password. */
    private Instant passwordChangedAt;

    // ─── Roles ───────────────────────────────────────────────────────────────

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        schema = "identity",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // ─── Derived helpers ──────────────────────────────────────────────────────

    /** Returns the user's full display name. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /** Checks whether the account is currently locked due to brute-force protection. */
    public boolean isAccountLocked() {
        if (lockedUntil == null) return false;
        return Instant.now().isBefore(lockedUntil);
    }

    /** Checks whether the account is fully active and can authenticate. */
    public boolean isActive() {
        return status == UserStatus.ACTIVE && !isAccountLocked();
    }

    /** Increments failed login counter and locks account at threshold. */
    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 10) {
            // Hard lock — requires admin review
            this.status = UserStatus.LOCKED;
        } else if (this.failedLoginAttempts >= 5) {
            // Temporary lock — 15 minutes
            this.lockedUntil = Instant.now().plusSeconds(900);
        }
    }

    /** Resets the failed login counter on successful login. */
    public void recordSuccessfulLogin(String ip) {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.lastLoginAt = Instant.now();
        this.lastLoginIp = ip;
    }
}
