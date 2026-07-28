package com.antigravity.sanab.notification.domain.repository;

import com.antigravity.sanab.notification.domain.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link NotificationPreference} entities.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    /**
     * Finds preferences for a specific user.
     * Returns empty if the user has not set custom preferences yet.
     */
    Optional<NotificationPreference> findByUserId(UUID userId);
}
