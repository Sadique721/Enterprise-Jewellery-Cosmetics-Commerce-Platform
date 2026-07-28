package com.antigravity.sanab.notification.infrastructure.provider.email;

import com.antigravity.sanab.notification.application.port.NotificationDeliveryException;
import com.antigravity.sanab.notification.domain.enums.ProviderType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * SMTP email provider using Spring's {@link JavaMailSender}.
 *
 * <p>Works with any SMTP relay: Gmail SMTP, AWS SES, SendGrid, Mailgun, or
 * self-hosted Postfix. Configuration is driven by Spring's standard
 * {@code spring.mail.*} properties.
 *
 * <p>Active when {@code sanab.notification.email.provider=smtp} (default).
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "sanab.notification.email.provider",
        havingValue = "smtp",
        matchIfMissing = true
)
public class SmtpEmailProvider implements EmailProvider {

    private final JavaMailSender mailSender;

    @Override
    public String sendHtml(String to, String subject, String htmlBody, String recipientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            // Personal name is not set here as it requires sender config

            mailSender.send(message);

            // SMTP does not return a message ID from JavaMail easily; generate a local one
            String localId = "smtp-" + System.currentTimeMillis();
            log.debug("Email sent via SMTP: to={}, subject={}, id={}", to, subject, localId);
            return localId;

        } catch (MessagingException ex) {
            log.error("SMTP email delivery failed: to={}, subject={}: {}", to, subject, ex.getMessage());
            throw new NotificationDeliveryException(
                    "SMTP delivery failed to %s: %s".formatted(to, ex.getMessage()), ex);
        }
    }

    @Override
    public String sendHtmlWithAttachment(String to, String subject, String htmlBody,
                                          String recipientName, String attachmentName,
                                          byte[] attachmentData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            // Attach the PDF
            helper.addAttachment(attachmentName,
                    () -> new java.io.ByteArrayInputStream(attachmentData),
                    "application/pdf");

            mailSender.send(message);

            String localId = "smtp-attach-" + System.currentTimeMillis();
            log.debug("Email with attachment sent: to={}, attachment={}", to, attachmentName);
            return localId;

        } catch (MessagingException ex) {
            log.error("SMTP email (with attachment) failed: to={}: {}", to, ex.getMessage());
            throw new NotificationDeliveryException(
                    "SMTP attachment delivery failed to %s: %s".formatted(to, ex.getMessage()), ex);
        }
    }

    @Override
    public ProviderType providerType() {
        return ProviderType.SMTP;
    }
}
