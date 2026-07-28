package com.antigravity.sanab.notification.application.port;

import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a notification delivery attempt fails.
 *
 * <p>Spring Retry intercepts this exception and applies the configured
 * retry policy (exponential backoff). After all retries are exhausted,
 * the notification is marked {@code PERMANENTLY_FAILED}.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public class NotificationDeliveryException extends SanabException {

    public NotificationDeliveryException(String message) {
        super(ErrorCode.NOTIFICATION_SEND_FAILED, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public NotificationDeliveryException(String message, Throwable cause) {
        super(ErrorCode.NOTIFICATION_SEND_FAILED, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
