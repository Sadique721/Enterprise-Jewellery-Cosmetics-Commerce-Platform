package com.antigravity.sanab.notification.application.service;

import com.antigravity.sanab.notification.api.dto.response.NotificationResponse;
import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.domain.enums.NotificationStatus;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Core service contract for the notification pipeline.
 *
 * <p>Orchestrates the entire notification lifecycle:
 * event reception → preference check → template rendering → channel dispatch → retry.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface NotificationService {

    /**
     * Processes a domain event and dispatches notifications to all applicable channels.
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Determine applicable channels (event defaults intersected with user preferences)</li>
     *   <li>Create a {@link Notification} record per channel (status: QUEUED)</li>
     *   <li>Render content via template engine</li>
     *   <li>Dispatch via channel adapter (with retry)</li>
     *   <li>Update notification status</li>
     *   <li>Audit log the outcome</li>
     * </ol>
     *
     * @param event the business domain event to process
     */
    void processEvent(SanabNotificationEvent event);

    /**
     * Dispatches a single notification to its channel.
     * Called internally by processEvent and by the retry scheduler.
     *
     * @param notificationId the ID of the notification to dispatch
     */
    void dispatch(UUID notificationId);

    /**
     * Marks a notification as read (for IN_APP channel).
     *
     * @param notificationId the notification ID
     * @param userId         the requesting user's ID (for ownership validation)
     */
    void markAsRead(UUID notificationId, UUID userId);

    /**
     * Marks all unread in-app notifications as read for a user.
     *
     * @param userId the user's UUID
     */
    void markAllAsRead(UUID userId);

    /**
     * Retrieves paginated notification history for a user.
     *
     * @param userId   the user's UUID
     * @param pageable pagination parameters
     * @return page of notifications
     */
    Page<NotificationResponse> getUserNotifications(UUID userId, Pageable pageable);

    /**
     * Returns the count of unread in-app notifications for a user.
     *
     * @param userId the user's UUID
     * @return unread count
     */
    long countUnread(UUID userId);

    /**
     * Retries all failed notifications that are eligible for retry.
     * Called by the scheduled retry job.
     */
    void retryFailedNotifications();
}
