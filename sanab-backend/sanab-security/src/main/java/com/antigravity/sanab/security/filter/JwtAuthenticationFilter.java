package com.antigravity.sanab.security.filter;

import com.antigravity.sanab.security.jwt.JwtBlacklist;
import com.antigravity.sanab.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter — executed once per HTTP request.
 *
 * <p>Processing pipeline:
 * <ol>
 *   <li>Extract Bearer token from {@code Authorization} header</li>
 *   <li>Validate token signature, expiry, and issuer</li>
 *   <li>Check JTI against Redis blacklist (revocation)</li>
 *   <li>Extract claims (userId, roles)</li>
 *   <li>Populate {@link SecurityContextHolder} with authentication</li>
 * </ol>
 *
 * <p>Unauthenticated requests pass through — endpoint-level authorization
 * is handled by Spring Security's filter chain configuration.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX         = "Bearer ";
    private static final String ROLE_PREFIX           = "ROLE_";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtBlacklist     jwtBlacklist;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            processToken(token, request);
        }

        filterChain.doFilter(request, response);
    }

    private void processToken(String token, HttpServletRequest request) {
        if (!jwtTokenProvider.validateAccessToken(token)) {
            log.debug("JWT validation failed for request: {}", request.getRequestURI());
            return;
        }

        // Check revocation (logout blacklist)
        String jti = jwtTokenProvider.extractJti(token);
        if (jwtBlacklist.isBlacklisted(jti)) {
            log.debug("JWT is blacklisted (revoked): jti={}", jti);
            return;
        }

        // Extract claims and build authentication object
        Claims claims = jwtTokenProvider.extractAccessClaims(token);
        String subject = claims.getSubject();

        Set<String> roles = jwtTokenProvider.extractRoles(token);
        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(
                        role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role))
                .collect(Collectors.toSet());

        var authentication = new UsernamePasswordAuthenticationToken(
                subject, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("JWT authentication set for userId={}, roles={}", subject, roles);
    }

    /**
     * Extracts the token value from the {@code Authorization: Bearer <token>} header.
     *
     * @return the token string, or null if not present / not a Bearer token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Skip filter for publicly accessible paths (avoids unnecessary token parsing).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/auth/forgot-password")
                || path.startsWith("/api/v1/auth/reset-password")
                || path.startsWith("/api/v1/auth/verify-email")
                || path.startsWith("/actuator/health")
                || path.startsWith("/actuator/info");
    }
}
