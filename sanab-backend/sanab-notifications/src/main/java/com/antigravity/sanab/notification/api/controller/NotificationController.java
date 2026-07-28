package com.antigravity.sanab.notification.api.controller;

import com.antigravity.sanab.notification.api.dto.request.UpdatePreferenceRequest;
import com.antigravity.sanab.notification.api.dto.response.NotificationPreferenceResponse;
import com.antigravity.sanab.notification.api.dto.response.NotificationResponse;
import com.antigravity.sanab.notification.application.service.NotificationPreferenceService;
import com.antigravity.sanab.notification.application.service.NotificationService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API controller for the notification module.
 *
 * <p>Exposes:
 * <ul>
 *   <li>Notification inbox (paginated in-app notifications)</li>
 *   <li>Mark as read / mark all as read</li>
 *   <li>Unread count</li>
 *   <li>Notification preferences CRUD</li>
 * </ul>
 *
 * <p>All endpoints require authentication (JWT). User identity is extracted
 * from the authentication principal.
 *
 * <p>Base path: {@code /api/v1/notifications}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification inbox and preference management")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;

    // ─── Notification Inbox ───────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get notification inbox",
            description = "Returns paginated in-app notifications for the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal String userIdStr,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        UUID userId = UUID.fromString(userIdStr);
        Page<NotificationResponse> page = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(page)));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString(userIdStr);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all in-app notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    // ─── Notification Preferences ─────────────────────────────────────────────

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences for the authenticated user")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> getPreferences(
            @AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(preferenceService.getPreferences(userId)));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update notification preferences for the authenticated user")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> updatePreferences(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody UpdatePreferenceRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(preferenceService.updatePreferences(userId, request)));
    }
}
