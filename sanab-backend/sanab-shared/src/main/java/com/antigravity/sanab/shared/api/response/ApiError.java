package com.antigravity.sanab.shared.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * Structured error detail embedded within {@link ApiResponse} on failure.
 *
 * <p>Provides machine-readable error codes alongside human-readable messages
 * and optional field-level validation errors.
 *
 * <p>Example:
 * <pre>{@code
 * {
 *   "code": "VALIDATION_FAILED",
 *   "message": "Request validation failed",
 *   "path": "/api/v1/auth/register",
 *   "fieldErrors": [
 *     { "field": "email", "message": "must be a well-formed email address" },
 *     { "field": "password", "message": "must be at least 12 characters" }
 *   ]
 * }
 * }</pre>
 *
 * @param code        machine-readable error code (e.g. VALIDATION_FAILED, USER_NOT_FOUND)
 * @param message     human-readable error summary
 * @param path        the request path where the error occurred
 * @param fieldErrors list of field-level validation errors; null when not applicable
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        String path,
        List<FieldError> fieldErrors
) {

    /**
     * Creates an {@link ApiError} without field-level details.
     */
    public static ApiError of(String code, String message, String path) {
        return new ApiError(code, message, path, null);
    }

    /**
     * Creates an {@link ApiError} with field-level validation details.
     */
    public static ApiError ofValidation(String message, String path, List<FieldError> fieldErrors) {
        return new ApiError("VALIDATION_FAILED", message, path, fieldErrors);
    }

    /**
     * Represents a single field-level validation error.
     *
     * @param field   the field name that failed validation
     * @param message the validation failure message
     * @param value   the rejected value; null if not applicable
     */
    @JsonInclude(Include.NON_NULL)
    public record FieldError(String field, String message, Object value) {

        public static FieldError of(String field, String message) {
            return new FieldError(field, message, null);
        }

        public static FieldError of(String field, String message, Object value) {
            return new FieldError(field, message, value);
        }
    }
}
