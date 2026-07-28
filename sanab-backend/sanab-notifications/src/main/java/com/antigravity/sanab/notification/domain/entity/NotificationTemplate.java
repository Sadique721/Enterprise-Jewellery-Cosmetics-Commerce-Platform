package com.antigravity.sanab.notification.domain.entity;

import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

/**
 * Versioned notification template for rendering channel-specific content.
 *
 * <p>Templates are identified by the combination of:
 * <ul>
 *   <li>{@link NotificationEventType} — which business event</li>
 *   <li>{@link NotificationChannel} — which delivery channel</li>
 *   <li>{@code locale} — BCP 47 language tag (default: "en")</li>
 * </ul>
 *
 * <p>Template versioning:
 * <ul>
 *   <li>Each template has a monotonically increasing version number</li>
 *   <li>Only one version is active ({@code isActive=true}) per event+channel+locale</li>
 *   <li>Old versions are retained for audit purposes, never deleted</li>
 * </ul>
 *
 * <p>Template syntax uses Thymeleaf. Variables are injected as a
 * {@code Map<String, Object>} keyed by the event's declared variable names.
 *
 * <p>Database: {@code notifications.notification_templates}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
        name = "notification_templates",
        schema = "notifications",
        indexes = {
                @Index(name = "idx_tmpl_event_channel_locale",
                        columnList = "event_type, channel, locale")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_tmpl_event_channel_locale_version",
                        columnNames = {"event_type", "channel", "locale", "version"}
                )
        }
)
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate extends BaseEntity {

    /** Human-readable template name for admin UI display. */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 60)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    /**
     * BCP 47 locale tag (e.g. "en", "ar", "hi").
     * Fallback locale is always "en".
     */
    @Column(name = "locale", nullable = false, length = 10)
    @Builder.Default
    private String locale = "en";

    /**
     * Monotonically increasing version number within an event+channel+locale scope.
     * Starts at 1.
     */
    @Column(name = "template_version", nullable = false)
    @Builder.Default
    private int templateVersion = 1;

    /**
     * Whether this template version is the active one.
     * Only one template per event+channel+locale may be active at a time.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Subject line template (for EMAIL channel only).
     * May contain Thymeleaf expressions: {@code Order #{orderId} Confirmed}.
     */
    @Column(name = "subject_template", length = 300)
    private String subjectTemplate;

    /**
     * The template body content.
     * <ul>
     *   <li>EMAIL: HTML with Thymeleaf syntax</li>
     *   <li>SMS: Plain text ≤ 160 characters (single part) or ≤ 1600 (multi-part)</li>
     *   <li>WHATSAPP: Text or approved template name + variables</li>
     *   <li>IN_APP: Short markdown text</li>
     * </ul>
     */
    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    /**
     * Optional external template file path relative to resources/templates/.
     * When set, bodyTemplate is loaded from this file path at startup.
     * Example: {@code email/order_placed.html}
     */
    @Column(name = "template_file_path", length = 300)
    private String templateFilePath;

    /** Optional description of what this template is for (admin documentation). */
    @Column(name = "description", length = 500)
    private String description;
}
