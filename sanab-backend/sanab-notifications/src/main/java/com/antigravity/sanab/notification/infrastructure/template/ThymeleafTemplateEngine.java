package com.antigravity.sanab.notification.infrastructure.template;

import com.antigravity.sanab.notification.application.port.TemplateEnginePort;
import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.domain.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

/**
 * Thymeleaf-backed implementation of {@link TemplateEnginePort}.
 *
 * <p>Template resolution strategy:
 * <ol>
 *   <li>Look up active template in DB by event+channel+locale</li>
 *   <li>If none found for the requested locale, fall back to "en"</li>
 *   <li>If the template has a {@code templateFilePath}, render from classpath file</li>
 *   <li>Otherwise, render the {@code bodyTemplate} string inline</li>
 * </ol>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThymeleafTemplateEngine implements TemplateEnginePort {

    private final TemplateEngine thymeleafEngine;
    private final NotificationTemplateRepository templateRepository;

    @Override
    public NotificationTemplate resolveTemplate(NotificationEventType eventType,
                                                 NotificationChannel channel,
                                                 String locale) {
        // 1. Try exact locale match
        return templateRepository
                .findByEventTypeAndChannelAndLocaleAndActiveTrue(eventType, channel, locale)
                .or(() -> {
                    // 2. Fall back to English
                    if (!"en".equals(locale)) {
                        log.debug("No template for locale={}, falling back to 'en' for event={}",
                                locale, eventType);
                        return templateRepository.findByEventTypeAndChannelAndLocaleAndActiveTrue(
                                eventType, channel, "en");
                    }
                    return java.util.Optional.empty();
                })
                .orElseThrow(() -> new TemplateNotFoundException(
                        "No active template found for event=%s, channel=%s, locale=%s"
                                .formatted(eventType, channel, locale)));
    }

    @Override
    public String renderSubject(NotificationTemplate template, Map<String, Object> variables) {
        if (template.getSubjectTemplate() == null || template.getSubjectTemplate().isBlank()) {
            return "SANAB Notification"; // Fallback subject
        }

        Context ctx = buildContext(variables, template.getLocale());
        return thymeleafEngine.process(
                wrapInlineTemplate(template.getSubjectTemplate()), ctx);
    }

    @Override
    public String renderBody(NotificationTemplate template, Map<String, Object> variables) {
        Context ctx = buildContext(variables, template.getLocale());

        if (template.getTemplateFilePath() != null && !template.getTemplateFilePath().isBlank()) {
            // Render from classpath file (resources/templates/{path})
            return thymeleafEngine.process(template.getTemplateFilePath(), ctx);
        }

        // Render inline body template string
        return thymeleafEngine.process(
                wrapInlineTemplate(template.getBodyTemplate()), ctx);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Context buildContext(Map<String, Object> variables, String localeTag) {
        Locale locale;
        try {
            locale = Locale.forLanguageTag(localeTag);
        } catch (Exception e) {
            locale = Locale.ENGLISH;
        }

        Context ctx = new Context(locale);
        if (variables != null) {
            ctx.setVariables(variables);
        }
        return ctx;
    }

    /**
     * Wraps a raw template string for processing by the Thymeleaf ClassicModeTemplateEngine.
     * Uses the special Thymeleaf inline processing marker.
     */
    private String wrapInlineTemplate(String templateText) {
        // Use string template resolver (requires configuring a StringTemplateResolver)
        return templateText;
    }
}
