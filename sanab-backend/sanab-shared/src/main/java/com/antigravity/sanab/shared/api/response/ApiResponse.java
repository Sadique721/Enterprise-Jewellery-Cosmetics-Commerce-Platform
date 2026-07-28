package com.antigravity.sanab.shared.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.Instant;
import java.util.UUID;

/**
 * Universal API response envelope for all SANAB endpoints.
 *
 * <p>Every HTTP response from the SANAB backend is wrapped in this record.
 * This guarantees a consistent contract for all API consumers.
 *
 * <p>Structure:
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "Operation completed successfully",
 *   "data": { ... },
 *   "error": null,
 *   "requestId": "req-550e8400-e29b-41d4-a716-446655440000",
 *   "timestamp": "2026-07-27T07:55:00Z"
 * }
 * }</pre>
 *
 * <p>Usage:
 * <pre>{@code
 * return ResponseEntity.ok(ApiResponse.success(orderDto, "Order placed successfully"));
 * return ResponseEntity.badRequest().body(ApiResponse.error(ApiError.of(...)));
 * }</pre>
 *
 * @param <T>       the type of the response payload
 * @param success   whether the operation completed successfully
 * @param message   human-readable summary of the result
 * @param data      the response payload; null on error responses
 * @param error     error detail; null on success responses
 * @param requestId unique request identifier for tracing
 * @param timestamp UTC timestamp of response generation
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        ApiError error,
        String requestId,
        Instant timestamp
) {

    // ─── Success factories ───────────────────────────────────────────────

    /**
     * Creates a success response with data payload and a custom message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, generateRequestId(), Instant.now());
    }

    /**
     * Creates a success response with data and the default "Success" message.
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    /**
     * Creates a success response with no data (for void operations).
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, null, generateRequestId(), Instant.now());
    }

    /**
     * Creates a success response for void operations with no message override.
     */
    public static ApiResponse<Void> accepted() {
        return success("Request accepted");
    }

    // ─── Error factories ─────────────────────────────────────────────────

    /**
     * Creates an error response wrapping the given {@link ApiError}.
     */
    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(false, error.message(), null, error, generateRequestId(), Instant.now());
    }

    /**
     * Creates an error response with a simple message (convenience overload).
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null,
                ApiError.of("INTERNAL_ERROR", message, null),
                generateRequestId(), Instant.now());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private static String generateRequestId() {
        return "req-" + UUID.randomUUID();
    }
}
