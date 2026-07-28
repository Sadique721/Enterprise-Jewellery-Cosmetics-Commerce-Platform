package com.antigravity.sanab.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Applies enterprise-grade security headers to every HTTP response.
 *
 * <p>These headers implement defense-in-depth per OWASP recommendations:
 * <ul>
 *   <li><b>HSTS</b>: Forces HTTPS for 1 year including subdomains</li>
 *   <li><b>CSP</b>: Restricts resource origins to prevent XSS</li>
 *   <li><b>X-Frame-Options</b>: Prevents clickjacking</li>
 *   <li><b>X-Content-Type-Options</b>: Prevents MIME sniffing</li>
 *   <li><b>Referrer-Policy</b>: Limits referrer leakage</li>
 *   <li><b>Permissions-Policy</b>: Disables dangerous browser features</li>
 *   <li><b>Cache-Control</b>: Prevents caching of API responses</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // HSTS — force HTTPS for 1 year, include subdomains, allow preload
        response.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains; preload");

        // CSP — restrict resource origins
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https://res.cloudinary.com; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'; " +
                "form-action 'self'; " +
                "base-uri 'self'");

        // Anti-clickjacking
        response.setHeader("X-Frame-Options", "DENY");

        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Disable XSS auditor (modern browsers don't need it — CSP is better)
        response.setHeader("X-XSS-Protection", "0");

        // Limit referrer info
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Disable dangerous browser features
        response.setHeader("Permissions-Policy",
                "geolocation=(), camera=(), microphone=(), payment=(), usb=()");

        // Prevent caching of API responses
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");

        // Remove server identification headers
        response.setHeader("Server", "");

        filterChain.doFilter(request, response);
    }
}
