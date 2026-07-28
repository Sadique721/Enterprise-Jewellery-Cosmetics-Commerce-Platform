package com.antigravity.sanab.notification.infrastructure.channel;

import com.antigravity.sanab.notification.application.port.NotificationChannelPort;
import com.antigravity.sanab.notification.application.port.TemplateEnginePort;
import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.ProviderType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Channel adapter for in-app notifications.
 *
 * <p>In-app notifications are persisted in the database and surfaced
 * to the user via the GET /api/v1/notifications endpoint. No external
 * provider is involved — the "send" is the database write itself.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InAppChannelAdapter implements NotificationChannelPort {

    private final TemplateEnginePort templateEngine;
    private final ObjectMapper objectMapper;

    @Override
    public void send(Notification notification) {
        Map<String, Object> variables = extractVariables(notification);

        try {
            NotificationTemplate template = templateEngine.resolveTemplate(
                    notification.getEventType(),
                    NotificationChannel.IN_APP,
                    notification.getLocale()
            );
            String renderedBody = templateEngine.renderBody(template, variables);
            notification.setContent(renderedBody);
        } catch (Exception ex) {
            // Fallback: use event type name as content if no template
            notification.setContent(notification.getEventType().getDisplayName());
            log.warn("Using fallback content for in-app notification id={}", notification.getId());
        }

        // "Sending" for in-app = the entity is already persisted in QUEUED state.
        // Marking as SENT here means it's visible to the user via the API.
        notification.markSent("in-app-" + notification.getId(), ProviderType.IN_APP_INTERNAL);

        log.debug("In-app notification stored: userId={}, eventType={}",
                notification.getUserId(), notification.getEventType());
    }

    @Override
    public boolean supports(Notification notification) {
        return notification.getChannel() == NotificationChannel.IN_APP;
    }

    private Map<String, Object> extractVariables(Notification notification) {
        try {
            if (notification.getMetadata() == null) return Map.of();
            return objectMapper.readValue(notification.getMetadata(), new TypeReference<>() {});
        } catch (Exception ex) {
            return Map.of();
        }
    }
}
