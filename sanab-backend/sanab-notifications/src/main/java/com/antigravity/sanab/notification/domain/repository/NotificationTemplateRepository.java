package com.antigravity.sanab.notification.domain.repository;

import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link NotificationTemplate} entities.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    /**
     * Finds the single active template for a given event+channel+locale combination.
     */
    Optional<NotificationTemplate> findByEventTypeAndChannelAndLocaleAndActiveTrue(
            NotificationEventType eventType, NotificationChannel channel, String locale);

    /**
     * Deactivates all templates for event+channel+locale before inserting a new version.
     * Ensures only one active version exists per combination.
     */
    @Modifying
    @Query("""
            UPDATE NotificationTemplate t
            SET t.active = false
            WHERE t.eventType = :eventType
              AND t.channel = :channel
              AND t.locale = :locale
              AND t.active = true
            """)
    void deactivateAllFor(@Param("eventType") NotificationEventType eventType,
                           @Param("channel") NotificationChannel channel,
                           @Param("locale") String locale);

    /**
     * Returns the highest version number for a given event+channel+locale,
     * used when creating the next version.
     */
    @Query("""
            SELECT MAX(t.version)
            FROM NotificationTemplate t
            WHERE t.eventType = :eventType
              AND t.channel = :channel
              AND t.locale = :locale
            """)
    Integer findMaxVersion(@Param("eventType") NotificationEventType eventType,
                            @Param("channel") NotificationChannel channel,
                            @Param("locale") String locale);
}
