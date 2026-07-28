package com.antigravity.sanab.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized JWT configuration properties.
 *
 * <p>All values are sourced from environment variables via {@code application.yml}.
 * No secrets are ever hardcoded.
 *
 * <pre>
 * sanab:
 *   security:
 *     jwt:
 *       secret: ${JWT_SECRET}
 *       refresh-secret: ${JWT_REFRESH_SECRET}
 *       access-token-expiration: ${JWT_ACCESS_TOKEN_EXPIRATION:900}
 *       refresh-token-expiration: ${JWT_REFRESH_TOKEN_EXPIRATION:2592000}
 * </pre>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sanab.security.jwt")
public class JwtProperties {

    /** HMAC-SHA512 secret for access tokens (min 512-bit / 64 chars). */
    private String secret;

    /** HMAC-SHA512 secret for refresh tokens (must differ from access secret). */
    private String refreshSecret;

    /** Access token TTL in seconds. Default: 900 (15 minutes). */
    private long accessTokenExpiration = 900L;

    /** Refresh token TTL in seconds. Default: 2592000 (30 days). */
    private long refreshTokenExpiration = 2_592_000L;

    /** Token issuer claim value. */
    private String issuer = "sanab.antigravity.com";
}
