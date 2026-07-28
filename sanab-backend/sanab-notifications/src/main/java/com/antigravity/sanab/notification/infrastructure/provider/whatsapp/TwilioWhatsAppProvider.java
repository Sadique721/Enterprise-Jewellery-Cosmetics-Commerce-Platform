package com.antigravity.sanab.notification.infrastructure.provider.whatsapp;

import com.antigravity.sanab.notification.application.port.NotificationDeliveryException;
import com.antigravity.sanab.notification.domain.enums.ProviderType;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Twilio WhatsApp provider implementation.
 *
 * <p>Sends WhatsApp messages via Twilio's WhatsApp API.
 * Uses the {@code whatsapp:} prefix on phone numbers as required by Twilio.
 *
 * <p>Active when {@code sanab.notification.whatsapp.provider=twilio} (default).
 *
 * <p>Required configuration:
 * <pre>
 * sanab:
 *   notification:
 *     whatsapp:
 *       provider: twilio
 *     twilio:
 *       account-sid: ${TWILIO_ACCOUNT_SID}
 *       auth-token: ${TWILIO_AUTH_TOKEN}
 *       whatsapp-from: +14155238886   # Twilio WhatsApp sandbox / Business number
 * </pre>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(
        name = "sanab.notification.whatsapp.provider",
        havingValue = "twilio",
        matchIfMissing = true
)
public class TwilioWhatsAppProvider implements WhatsAppProvider {

    private static final String WHATSAPP_PREFIX = "whatsapp:";

    @Value("${sanab.notification.twilio.account-sid}")
    private String accountSid;

    @Value("${sanab.notification.twilio.auth-token}")
    private String authToken;

    @Value("${sanab.notification.twilio.whatsapp-from}")
    private String fromNumber;

    @PostConstruct
    private void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio WhatsApp provider initialized with from=whatsapp:{}", fromNumber);
    }

    @Override
    public String sendMessage(String to, String body) {
        // Twilio requires the "whatsapp:" prefix on both from and to
        String twilioTo   = WHATSAPP_PREFIX + to;
        String twilioFrom = WHATSAPP_PREFIX + fromNumber;

        try {
            Message message = Message.creator(
                            new PhoneNumber(twilioTo),
                            new PhoneNumber(twilioFrom),
                            body)
                    .create();

            log.debug("WhatsApp message sent via Twilio: to={}, sid={}, status={}",
                    to, message.getSid(), message.getStatus());

            return message.getSid();

        } catch (ApiException ex) {
            log.error("Twilio WhatsApp delivery failed: to={}, error={} (code={})",
                    to, ex.getMessage(), ex.getCode());
            throw new NotificationDeliveryException(
                    "Twilio WhatsApp failed to %s: [%d] %s".formatted(to, ex.getCode(), ex.getMessage()), ex);
        }
    }

    @Override
    public ProviderType providerType() {
        return ProviderType.TWILIO_WHATSAPP;
    }
}
