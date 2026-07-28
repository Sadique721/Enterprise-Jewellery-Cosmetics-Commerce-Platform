package com.antigravity.sanab.notification.application.port;

import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;

import java.util.Map;

/**
 * Port for the template rendering engine.
 *
 * <p>Decouples the notification service from the concrete Thymeleaf implementation.
 * Can be swapped for any other template engine (Freemarker, Mustache, etc.)
 * without changing business logic.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public interface TemplateEnginePort {

    /**
     * Renders the subject line template using the provided variables.
     *
     * @param template  the notification template
     * @param variables the context variables for interpolation
     * @return the rendered subject string
     */
    String renderSubject(NotificationTemplate template, Map<String, Object> variables);

    /**
     * Renders the body template using the provided variables.
     *
     * @param template  the notification template
     * @param variables the context variables for interpolation
     * @return the rendered body string (HTML for email, plain text for SMS/WhatsApp)
     */
    String renderBody(NotificationTemplate template, Map<String, Object> variables);

    /**
     * Resolves the active template for a given event, channel, and locale.
     *
     * <p>Falls back to "en" locale if no template exists for the requested locale.
     *
     * @param eventType the business event type
     * @param channel   the delivery channel
     * @param locale    the preferred locale (BCP 47)
     * @return the active template
     * @throws TemplateNotFoundException if no template is found for event+channel
     */
    NotificationTemplate resolveTemplate(NotificationEventType eventType,
                                         NotificationChannel channel,
                                         String locale);
}
