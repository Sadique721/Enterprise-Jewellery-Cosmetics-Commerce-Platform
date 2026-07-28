package com.antigravity.sanab.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Redis-backed JWT blacklist for access token revocation.
 *
 * <p>When a user logs out, the access token's JTI is added to Redis
 * with a TTL equal to the token's remaining validity. This ensures:
 * <ul>
 *   <li>Revoked tokens are rejected on every request</li>
 *   <li>Redis entries auto-expire when tokens would have expired anyway</li>
 *   <li>No memory leak — the blacklist never grows unbounded</li>
 * </ul>
 *
 * <p>Key format: {@code sanab:jwt:blacklist:{jti}}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtBlacklist {

    private static final String BLACKLIST_PREFIX = "sanab:jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    /**
     * Adds a token JTI to the blacklist.
     *
     * @param jti    the unique token ID (JWT "jti" claim)
     * @param expiry the token's expiry instant (used to compute TTL)
     */
    public void blacklist(String jti, Instant expiry) {
        Duration ttl = Duration.between(Instant.now(), expiry);
        if (ttl.isNegative() || ttl.isZero()) {
            // Token is already expired — no need to blacklist
            return;
        }
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, "1", ttl);
        log.debug("Token blacklisted: jti={}, ttl={}s", jti, ttl.getSeconds());
    }

    /**
     * Checks whether a token JTI is blacklisted.
     *
     * @param jti the unique token ID
     * @return true if the token has been revoked
     */
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    }
}
