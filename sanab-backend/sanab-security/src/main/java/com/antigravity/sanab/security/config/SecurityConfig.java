package com.antigravity.sanab.security.config;

import com.antigravity.sanab.security.filter.CorrelationIdFilter;
import com.antigravity.sanab.security.filter.JwtAuthenticationFilter;
import com.antigravity.sanab.security.filter.RateLimitingFilter;
import com.antigravity.sanab.security.filter.SecurityHeadersFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Enterprise Spring Security configuration for SANAB.
 *
 * <p>Key design decisions:
 * <ul>
 *   <li><b>Stateless</b>: No HTTP sessions — JWT-only authentication</li>
 *   <li><b>CSRF disabled</b>: Appropriate for stateless REST APIs with JWT</li>
 *   <li><b>Method security</b>: {@code @PreAuthorize} enabled for fine-grained access control</li>
 *   <li><b>Filter order</b>: Correlation → RateLimit → SecurityHeaders → JWT → Authorization</li>
 *   <li><b>Argon2id</b>: Used for password hashing (memory-hard, phishing-resistant)</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorrelationIdFilter     correlationIdFilter;
    private final RateLimitingFilter      rateLimitingFilter;
    private final SecurityHeadersFilter   securityHeadersFilter;
    private final ObjectMapper            objectMapper;

    @Value("${sanab.cors.allowed-origins:http://localhost:3000}")
    private List<String> allowedOrigins;

    // ─── Security Filter Chain ────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — stateless API uses JWT, not cookies
            .csrf(AbstractHttpConfigurer::disable)

            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Stateless session — no HttpSession ever created
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Endpoint authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public auth endpoints
                .requestMatchers(HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/reset-password",
                        "/api/v1/auth/verify-email",
                        "/api/v1/auth/otp/send",
                        "/api/v1/auth/otp/verify"
                ).permitAll()

                // Public product browsing
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/products/**",
                        "/api/v1/categories/**",
                        "/api/v1/brands/**",
                        "/api/v1/search/**"
                ).permitAll()

                // Health & metrics
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // OpenAPI docs (development)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Admin-only endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Custom filter ordering:
            // CorrelationId → RateLimit → SecurityHeaders → JWT → Spring's auth filter
            .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitingFilter, CorrelationIdFilter.class)
            .addFilterBefore(securityHeadersFilter, RateLimitingFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // Custom 401 / 403 responses with JSON body
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                            "success", false,
                            "message", "Authentication required",
                            "errorCode", "UNAUTHORIZED"
                    )));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                            "success", false,
                            "message", "Access denied: insufficient permissions",
                            "errorCode", "FORBIDDEN"
                    )));
                })
            );

        return http.build();
    }

    // ─── CORS ─────────────────────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-Correlation-Id", "X-Requested-With"));
        config.setExposedHeaders(List.of("X-Correlation-Id", "X-Total-Count"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // ─── Password Encoder ─────────────────────────────────────────────────────

    /**
     * Argon2id password encoder — memory-hard, state-of-the-art hashing.
     *
     * <p>Parameters:
     * <ul>
     *   <li>Salt length: 16 bytes</li>
     *   <li>Hash length: 32 bytes</li>
     *   <li>Parallelism: 1</li>
     *   <li>Memory: 65536 KB (64 MB)</li>
     *   <li>Iterations: 3</li>
     * </ul>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }
}
