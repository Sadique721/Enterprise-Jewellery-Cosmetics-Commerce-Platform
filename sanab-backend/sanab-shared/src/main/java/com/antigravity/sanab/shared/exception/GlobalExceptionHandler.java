package com.antigravity.sanab.shared.exception;

import com.antigravity.sanab.shared.api.response.ApiError;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for all controllers in the SANAB platform.
 *
 * <p>Intercepts all exceptions thrown from controllers and converts them into
 * standardized {@link ApiResponse} error responses. This ensures every error
 * response follows the same JSON structure.
 *
 * <p>Handles:
 * <ul>
 *   <li>SANAB business exceptions ({@link SanabException} subtypes)</li>
 *   <li>Spring Validation exceptions</li>
 *   <li>Spring Security exceptions</li>
 *   <li>Spring MVC exceptions</li>
 *   <li>JPA/Hibernate exceptions</li>
 *   <li>Uncaught runtime exceptions (fallback)</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── SANAB Business Exceptions ───────────────────────────────────────────

    /**
     * Handles all SANAB-specific business exceptions.
     * Maps the exception's HTTP status and error code to the response.
     */
    @ExceptionHandler(SanabException.class)
    public ResponseEntity<ApiResponse<Void>> handleSanabException(
            SanabException ex, HttpServletRequest request) {

        log.warn("Business exception on [{}] {}: {} — {}",
                request.getMethod(), request.getRequestURI(),
                ex.getErrorCode(), ex.getMessage());

        var apiError = ApiError.of(
                ex.getErrorCode().name(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(apiError));
    }

    // ─── Spring Validation ───────────────────────────────────────────────────

    /**
     * Handles {@code @Valid} annotation failures on request bodies.
     * Returns all field-level errors in the response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fe) {
                        return ApiError.FieldError.of(
                                fe.getField(),
                                fe.getDefaultMessage(),
                                fe.getRejectedValue()
                        );
                    }
                    return ApiError.FieldError.of(error.getObjectName(), error.getDefaultMessage());
                })
                .toList();

        var apiError = ApiError.ofValidation(
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        log.debug("Validation failed on {} {}: {} field error(s)",
                request.getMethod(), request.getRequestURI(), fieldErrors.size());

        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    /**
     * Handles path variable / request parameter constraint violations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<ApiError.FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> ApiError.FieldError.of(
                        extractFieldName(v),
                        v.getMessage(),
                        v.getInvalidValue()
                ))
                .toList();

        var apiError = ApiError.ofValidation("Constraint violation", request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    // ─── Spring MVC ──────────────────────────────────────────────────────────

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        var apiError = ApiError.of(
                ErrorCode.METHOD_NOT_ALLOWED.name(),
                "HTTP method '%s' is not supported for this endpoint".formatted(ex.getMethod()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

        var apiError = ApiError.of(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.name(),
                "Media type '%s' is not supported".formatted(ex.getContentType()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        var apiError = ApiError.of(
                ErrorCode.VALIDATION_FAILED.name(),
                "Required parameter '%s' is missing".formatted(ex.getParameterName()),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        var apiError = ApiError.of(
                ErrorCode.VALIDATION_FAILED.name(),
                "Parameter '%s' has invalid value: %s".formatted(ex.getName(), ex.getValue()),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        var apiError = ApiError.of(
                ErrorCode.BAD_REQUEST.name(),
                "Request body is malformed or missing",
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(ApiResponse.error(apiError));
    }

    // ─── Spring Security ─────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied on [{}] {}", request.getMethod(), request.getRequestURI());
        var apiError = ApiError.of(
                ErrorCode.ACCESS_DENIED.name(),
                "You do not have permission to perform this action",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {

        var apiError = ApiError.of(
                ErrorCode.UNAUTHORIZED.name(),
                "Authentication is required",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(apiError));
    }

    // ─── JPA / Database ──────────────────────────────────────────────────────

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(
            OptimisticLockingFailureException ex, HttpServletRequest request) {

        log.warn("Optimistic locking conflict on [{}] {}", request.getMethod(), request.getRequestURI());
        var apiError = ApiError.of(
                ErrorCode.OPTIMISTIC_LOCK_CONFLICT.name(),
                ErrorCode.OPTIMISTIC_LOCK_CONFLICT.getDefaultMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("Data integrity violation on [{}] {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        var apiError = ApiError.of(
                ErrorCode.RESOURCE_ALREADY_EXISTS.name(),
                "A resource with the provided data already exists",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(apiError));
    }

    // ─── Catch-all ───────────────────────────────────────────────────────────

    /**
     * Last-resort handler for all uncaught exceptions.
     * Logs the full stack trace and returns a safe generic error response.
     * Never exposes internal details to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error on [{}] {}: ",
                request.getMethod(), request.getRequestURI(), ex);

        var apiError = ApiError.of(
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(ApiResponse.error(apiError));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }
}
