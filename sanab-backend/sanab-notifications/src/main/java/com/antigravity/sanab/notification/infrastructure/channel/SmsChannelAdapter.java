package com.antigravity.sanab.notification.infrastructure.channel;

import com.antigravity.sanab.notification.application.port.NotificationChannelPort;
import com.antigravity.sanab.notification.application.port.TemplateEnginePort;
import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.infrastructure.provider.sms.SmsProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Channel adapter for SMS delivery.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsChannelAdapter implements NotificationChannelPort {

    private final SmsProvider smsProvider;
    private final TemplateEnginePort templateEngine;
    private final ObjectMapper objectMapper;

    @Override
    public void send(Notification notification) {
        Map<String, Object> variables = extractVariables(notification);

        NotificationTemplate template = templateEngine.resolveTemplate(
                notification.getEventType(),
                NotificationChannel.SMS,
                notification.getLocale()
        );

        String renderedBody = templateEngine.renderBody(template, variables);
        notification.setContent(renderedBody);

        String sid = smsProvider.sendSms(notification.getRecipientAddress(), renderedBody);
        notification.markSent(sid, smsProvider.providerType());

        log.debug("SMS dispatched: to={}, eventType={}, sid={}",
                notification.getRecipientAddress(), notification.getEventType(), sid);
    }

    @Override
    public boolean supports(Notification notification) {
        return notification.getChannel() == NotificationChannel.SMS
                && notification.getRecipientAddress() != null
                && notification.getRecipientAddress().startsWith("+");
    }

    private Map<String, Object> extractVariables(Notification notification) {
        try {
            if (notification.getMetadata() == null) return Map.of();
            return objectMapper.readValue(notification.getMetadata(), new TypeReference<>() {});
        } catch (Exception ex) {
            log.warn("Failed to deserialize SMS notification metadata: {}", ex.getMessage());
            return Map.of();
        }
    }
}
