package com.antigravity.sanab.identity.domain.repository;

import com.antigravity.sanab.identity.domain.entity.User;
import com.antigravity.sanab.identity.domain.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link User} entities.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true, u.status = 'ACTIVE' WHERE u.id = :id")
    void markEmailVerified(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE User u SET u.phoneVerified = true WHERE u.id = :id")
    void markPhoneVerified(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE User u SET u.passwordHash = :hash, u.passwordChangedAt = :changedAt WHERE u.id = :id")
    void updatePassword(@Param("id") UUID id,
                        @Param("hash") String hash,
                        @Param("changedAt") Instant changedAt);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0, u.lockedUntil = null WHERE u.id = :id")
    void resetLoginAttempts(@Param("id") UUID id);

    long countByStatus(UserStatus status);
}
