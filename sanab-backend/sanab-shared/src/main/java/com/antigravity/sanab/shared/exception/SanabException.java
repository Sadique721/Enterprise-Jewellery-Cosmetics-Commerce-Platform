package com.antigravity.sanab.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Root exception class for all SANAB business and technical exceptions.
 *
 * <p>Can be instantiated directly or extended by domain-specific exceptions.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Getter
public class SanabException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public SanabException(ErrorCode errorCode, String message) {
        this(errorCode, message, mapStatus(errorCode));
    }

    public SanabException(ErrorCode errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public SanabException(ErrorCode errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public SanabException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode, message, mapStatus(errorCode), cause);
    }

    private static HttpStatus mapStatus(ErrorCode code) {
        if (code == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        return switch (code) {
            case USER_NOT_FOUND, PRODUCT_NOT_FOUND, CATEGORY_NOT_FOUND, BRAND_NOT_FOUND,
                 ORDER_NOT_FOUND, ADDRESS_NOT_FOUND, CART_NOT_FOUND, RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED, INVALID_CREDENTIALS, INVALID_TOKEN, TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED, ACCOUNT_LOCKED, ACCOUNT_SUSPENDED -> HttpStatus.FORBIDDEN;
            case USER_ALREADY_EXISTS, EMAIL_ALREADY_EXISTS, PHONE_ALREADY_EXISTS,
                 PRODUCT_ALREADY_EXISTS, RESOURCE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
