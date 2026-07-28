package com.antigravity.sanab.security.jwt;

import com.antigravity.sanab.security.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * Core JWT token provider for SANAB.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Issue signed HMAC-SHA512 access tokens</li>
 *   <li>Issue signed HMAC-SHA512 refresh tokens</li>
 *   <li>Validate and parse tokens</li>
 *   <li>Extract claims (userId, roles, jti, expiry)</li>
 * </ul>
 *
 * <p><strong>Security decisions:</strong>
 * <ul>
 *   <li>HMAC-SHA512 — symmetric, fast, secure for server-side tokens</li>
 *   <li>Access and refresh tokens use different signing keys</li>
 *   <li>Every token embeds a unique JTI (JWT ID) for blacklisting</li>
 *   <li>Roles embedded in access token to avoid DB lookup per request</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID  = "uid";
    private static final String CLAIM_ROLES    = "roles";
    private static final String CLAIM_EMAIL    = "email";
    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TYPE_ACCESS    = "access";
    private static final String TYPE_REFRESH   = "refresh";

    private final JwtProperties jwtProperties;

    private SecretKey accessKey;
    private SecretKey refreshKey;

    @PostConstruct
    private void init() {
        accessKey  = Keys.hmacShaKeyFor(jwtProperties.getSecret()
                .getBytes(StandardCharsets.UTF_8));
        refreshKey = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret()
                .getBytes(StandardCharsets.UTF_8));
    }

    // ─── Token Issuance ───────────────────────────────────────────────────────

    /**
     * Issues a new signed access token.
     *
     * @param userId    the user's UUID (becomes the JWT subject)
     * @param email     the user's email (embedded claim)
     * @param roles     set of granted role names
     * @return signed, compact JWT string
     */
    public String issueAccessToken(UUID userId, String email, Set<String> roles) {
        Instant now    = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())           // JTI — unique per token
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .claim(CLAIM_USER_ID, userId.toString())
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TOKEN_TYPE, TYPE_ACCESS)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(accessKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Issues a new signed refresh token.
     *
     * @param userId    the user's UUID
     * @param sessionId the session ID (for family tracking / replay detection)
     * @return signed, compact JWT string
     */
    public String issueRefreshToken(UUID userId, UUID sessionId) {
        Instant now    = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .claim(CLAIM_USER_ID, userId.toString())
                .claim(CLAIM_TOKEN_TYPE, TYPE_REFRESH)
                .claim("sid", sessionId.toString())         // Session family ID
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(refreshKey, Jwts.SIG.HS512)
                .compact();
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    /**
     * Validates an access token's signature, expiry, and issuer.
     *
     * @param token the compact JWT string
     * @return true if valid; false otherwise
     */
    public boolean validateAccessToken(String token) {
        return validate(token, accessKey);
    }

    /**
     * Validates a refresh token's signature and expiry.
     *
     * @param token the compact JWT string
     * @return true if valid; false otherwise
     */
    public boolean validateRefreshToken(String token) {
        return validate(token, refreshKey);
    }

    private boolean validate(String token, SecretKey key) {
        try {
            parseToken(token, key);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("JWT expired: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("JWT malformed: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("JWT unsupported: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT empty/null: {}", ex.getMessage());
        }
        return false;
    }

    // ─── Claims Extraction ────────────────────────────────────────────────────

    /** Extracts all claims from an access token. */
    public Claims extractAccessClaims(String token) {
        return parseToken(token, accessKey).getPayload();
    }

    /** Extracts all claims from a refresh token. */
    public Claims extractRefreshClaims(String token) {
        return parseToken(token, refreshKey).getPayload();
    }

    /** Extracts the user UUID from an access token subject claim. */
    public UUID extractUserId(String token) {
        return UUID.fromString(extractAccessClaims(token).getSubject());
    }

    /** Extracts the JTI (unique token ID) from an access token. */
    public String extractJti(String token) {
        return extractAccessClaims(token).getId();
    }

    /** Extracts the token expiry as an Instant. */
    public Instant extractExpiry(String token) {
        return extractAccessClaims(token).getExpiration().toInstant();
    }

    /** Extracts the set of role names from an access token. */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Object roles = extractAccessClaims(token).get(CLAIM_ROLES);
        if (roles instanceof Collection<?> c) {
            return new HashSet<>((Collection<String>) c);
        }
        return Set.of();
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private Jws<Claims> parseToken(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token);
    }
}
