package com.antigravity.sanab.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Injects a unique correlation ID into every HTTP request for distributed tracing.
 *
 * <p>The correlation ID is:
 * <ul>
 *   <li>Read from incoming {@code X-Correlation-Id} header (if present — from API gateway)</li>
 *   <li>Generated as a new UUID (if not present)</li>
 *   <li>Stored in MDC for structured logging</li>
 *   <li>Added to every response via {@code X-Correlation-Id} header</li>
 * </ul>
 *
 * <p>This ensures every log line in a request's lifecycle carries the same ID,
 * enabling full trace reconstruction in production log aggregators.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_KEY               = "correlationId";
    public static final String REQUEST_ID_MDC_KEY    = "requestId";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String requestId = UUID.randomUUID().toString();

        MDC.put(MDC_KEY, correlationId);
        MDC.put(REQUEST_ID_MDC_KEY, requestId);

        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clear MDC to prevent thread-local leaks in thread pools
            MDC.remove(MDC_KEY);
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }
}
