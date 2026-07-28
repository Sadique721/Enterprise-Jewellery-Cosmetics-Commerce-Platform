package com.antigravity.sanab.notification.infrastructure.channel;

import com.antigravity.sanab.notification.application.port.NotificationChannelPort;
import com.antigravity.sanab.notification.application.port.NotificationDeliveryException;
import com.antigravity.sanab.notification.application.port.TemplateEnginePort;
import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.ProviderType;
import com.antigravity.sanab.notification.infrastructure.provider.email.EmailProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Channel adapter for email delivery.
 *
 * <p>Bridges between the generic {@link NotificationChannelPort} and the
 * concrete {@link EmailProvider} implementation. Handles template rendering
 * and passes the final HTML to the provider.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannelAdapter implements NotificationChannelPort {

    private final EmailProvider emailProvider;
    private final TemplateEnginePort templateEngine;
    private final ObjectMapper objectMapper;

    @Override
    public void send(Notification notification) {
        // Deserialize metadata into template variables map
        Map<String, Object> variables = extractVariables(notification);

        // Resolve template (may throw TemplateNotFoundException → caught above)
        NotificationTemplate template = templateEngine.resolveTemplate(
                notification.getEventType(),
                NotificationChannel.EMAIL,
                notification.getLocale()
        );

        String renderedSubject = templateEngine.renderSubject(template, variables);
        String renderedBody    = templateEngine.renderBody(template, variables);

        // Update notification with rendered content for audit
        notification.setSubject(renderedSubject);
        notification.setContent(renderedBody);

        // Dispatch via provider
        String providerMessageId = emailProvider.sendHtml(
                notification.getRecipientAddress(),
                renderedSubject,
                renderedBody,
                notification.getRecipientName()
        );

        notification.markSent(providerMessageId, emailProvider.providerType());
        log.debug("Email dispatched: to={}, eventType={}, messageId={}",
                notification.getRecipientAddress(), notification.getEventType(), providerMessageId);
    }

    @Override
    public boolean supports(Notification notification) {
        return notification.getChannel() == NotificationChannel.EMAIL
                && notification.getRecipientAddress() != null
                && notification.getRecipientAddress().contains("@");
    }

    private Map<String, Object> extractVariables(Notification notification) {
        try {
            if (notification.getMetadata() == null) return Map.of();
            return objectMapper.readValue(notification.getMetadata(), new TypeReference<>() {});
        } catch (Exception ex) {
            log.warn("Failed to deserialize notification metadata id={}: {}",
                    notification.getId(), ex.getMessage());
            return Map.of();
        }
    }
}
