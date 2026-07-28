package com.antigravity.sanab.notification.application.service;

import com.antigravity.sanab.notification.api.dto.request.UpdatePreferenceRequest;
import com.antigravity.sanab.notification.api.dto.response.NotificationPreferenceResponse;
import com.antigravity.sanab.notification.domain.entity.NotificationPreference;

import java.util.UUID;

/**
 * Service contract for managing user notification preferences.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface NotificationPreferenceService {

    /**
     * Retrieves current preferences for a user.
     * Creates default preferences (all enabled) if none exist.
     *
     * @param userId the user's UUID
     * @return current notification preferences
     */
    NotificationPreferenceResponse getPreferences(UUID userId);

    /**
     * Retrieves the raw preference entity for internal use.
     * Creates defaults if none exist.
     *
     * @param userId the user's UUID
     * @return preference entity
     */
    NotificationPreference getOrCreatePreference(UUID userId);

    /**
     * Updates user notification preferences.
     *
     * @param userId  the user's UUID
     * @param request the desired preference updates
     * @return updated preferences
     */
    NotificationPreferenceResponse updatePreferences(UUID userId, UpdatePreferenceRequest request);
}
