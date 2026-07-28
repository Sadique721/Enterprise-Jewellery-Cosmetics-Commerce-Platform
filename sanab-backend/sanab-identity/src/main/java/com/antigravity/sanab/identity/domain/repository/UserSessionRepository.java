package com.antigravity.sanab.identity.domain.repository;

import com.antigravity.sanab.identity.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link UserSession} entities.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByRefreshTokenHashAndActiveTrue(String refreshTokenHash);

    List<UserSession> findByUserIdAndActiveTrue(UUID userId);

    /** Invalidates all sessions for a user (logout-all / token replay detected). */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.userId = :userId")
    void revokeAllSessions(@Param("userId") UUID userId);

    /** Invalidates all sessions in the same family (replay attack response). */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.familyId = :familyId")
    void revokeFamilySessions(@Param("familyId") String familyId);

    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.id = :sessionId AND s.userId = :userId")
    void revokeSession(@Param("sessionId") UUID sessionId, @Param("userId") UUID userId);

    long countByUserIdAndActiveTrue(UUID userId);
}
