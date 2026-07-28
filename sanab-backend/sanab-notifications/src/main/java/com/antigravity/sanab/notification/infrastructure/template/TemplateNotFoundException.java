package com.antigravity.sanab.notification.infrastructure.template;

import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when no active template is found for a given event+channel+locale combination.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public class TemplateNotFoundException extends SanabException {

    public TemplateNotFoundException(String message) {
        super(ErrorCode.TEMPLATE_NOT_FOUND, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
