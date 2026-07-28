package com.antigravity.sanab.notification.domain.repository;

import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Notification} entities.
 *
 * <p>Custom queries avoid N+1 issues and are optimised for the
 * access patterns defined in the notification service.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Finds a notification by ID and user ID (for ownership validation).
     */
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Returns paginated in-app notifications for a user, newest first.
     */
    Page<Notification> findByUserIdAndChannelOrderByCreatedAtDesc(
            UUID userId, NotificationChannel channel, Pageable pageable);

    /**
     * Counts unread in-app notifications for a user.
     */
    long countByUserIdAndChannelAndReadFalse(UUID userId, NotificationChannel channel);

    /**
     * Bulk-marks all in-app notifications as read for a user.
     */
    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.read = true, n.readAt = :readAt, n.status = 'READ'
            WHERE n.userId = :userId
              AND n.channel = 'IN_APP'
              AND n.read = false
            """)
    void markAllReadForUser(@Param("userId") UUID userId,
                             @Param("readAt") Instant readAt);

    /**
     * Finds all notifications eligible for retry.
     *
     * <p>A notification is retryable when:
     * <ul>
     *   <li>Status is FAILED</li>
     *   <li>nextRetryAt is in the past (i.e., retry time has arrived)</li>
     *   <li>retryCount < maxRetries</li>
     * </ul>
     */
    @Query("""
            SELECT n FROM Notification n
            WHERE n.status = 'FAILED'
              AND n.nextRetryAt IS NOT NULL
              AND n.nextRetryAt <= :now
              AND n.retryCount < n.maxRetries
            ORDER BY n.priority ASC, n.nextRetryAt ASC
            """)
    List<Notification> findRetryableNotifications(@Param("now") Instant now);

    /**
     * Counts notifications by status (for admin monitoring dashboard).
     */
    long countByStatus(NotificationStatus status);

    /**
     * Returns delivery stats per channel (for admin analytics).
     */
    @Query("""
            SELECT n.channel, n.status, COUNT(n)
            FROM Notification n
            WHERE n.createdAt >= :from
            GROUP BY n.channel, n.status
            """)
    List<Object[]> deliveryStatsByChannelAndStatus(@Param("from") Instant from);
}
