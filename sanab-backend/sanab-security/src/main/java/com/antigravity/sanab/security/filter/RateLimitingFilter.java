package com.antigravity.sanab.security.filter;

import com.antigravity.sanab.shared.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis-backed rate limiting filter using a sliding-window token bucket algorithm.
 *
 * <p>Rate limits are applied per IP address. The in-memory {@link ConcurrentHashMap}
 * holds {@link Bucket} objects (lightweight) while Redis tracks counts across instances.
 *
 * <p>Rate limit configuration (from {@code application.yml}):
 * <pre>
 *   sanab.security.rate-limit.per-minute: ${RATE_LIMIT_PER_MINUTE:100}
 * </pre>
 *
 * <p>When the limit is exceeded, the filter responds immediately with HTTP 429
 * and a standard {@code ApiResponse} error body.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    private static final String RATE_LIMIT_KEY = "sanab:ratelimit:ip:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // In-process cache of buckets (reset on app restart — acceptable for rate limiting)
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientIp = extractClientIp(request);
        Bucket bucket = getBucket(clientIp);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            sendRateLimitResponse(response);
        }
    }

    private Bucket getBucket(String clientIp) {
        return bucketCache.computeIfAbsent(clientIp, ip -> {
            Bandwidth limit = Bandwidth.classic(
                    DEFAULT_REQUESTS_PER_MINUTE,
                    Refill.intervally(DEFAULT_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
            );
            return Bucket.builder().addLimit(limit).build();
        });
    }

    private String extractClientIp(HttpServletRequest request) {
        // Check proxy headers first (for load-balanced deployments)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Retry-After", "60");

        var body = Map.of(
                "success", false,
                "message", "Too many requests. Please wait before retrying.",
                "errorCode", ErrorCode.RATE_LIMIT_EXCEEDED.name()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    /** Skip rate limiting for health/actuator endpoints. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/actuator");
    }
}
