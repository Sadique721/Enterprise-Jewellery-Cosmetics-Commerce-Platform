package com.antigravity.sanab.notification.infrastructure.provider.sms;

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
 * Twilio SMS provider implementation.
 *
 * <p>Sends SMS via Twilio Programmable Messaging API.
 * Active when {@code sanab.notification.sms.provider=twilio} (default).
 *
 * <p>Required configuration:
 * <pre>
 * sanab:
 *   notification:
 *     sms:
 *       provider: twilio
 *     twilio:
 *       account-sid: ${TWILIO_ACCOUNT_SID}
 *       auth-token: ${TWILIO_AUTH_TOKEN}
 *       sms-from: +1XXXXXXXXXX
 * </pre>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(
        name = "sanab.notification.sms.provider",
        havingValue = "twilio",
        matchIfMissing = true
)
public class TwilioSmsProvider implements SmsProvider {

    @Value("${sanab.notification.twilio.account-sid}")
    private String accountSid;

    @Value("${sanab.notification.twilio.auth-token}")
    private String authToken;

    @Value("${sanab.notification.twilio.sms-from}")
    private String fromNumber;

    @PostConstruct
    private void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio SMS provider initialized with from={}", fromNumber);
    }

    @Override
    public String sendSms(String to, String body) {
        try {
            Message message = Message.creator(
                            new PhoneNumber(to),
                            new PhoneNumber(fromNumber),
                            body)
                    .create();

            log.debug("SMS sent via Twilio: to={}, sid={}, status={}",
                    to, message.getSid(), message.getStatus());

            return message.getSid();

        } catch (ApiException ex) {
            log.error("Twilio SMS delivery failed: to={}, error={} (code={})",
                    to, ex.getMessage(), ex.getCode());
            throw new NotificationDeliveryException(
                    "Twilio SMS failed to %s: [%d] %s".formatted(to, ex.getCode(), ex.getMessage()), ex);
        }
    }

    @Override
    public ProviderType providerType() {
        return ProviderType.TWILIO_SMS;
    }
}
