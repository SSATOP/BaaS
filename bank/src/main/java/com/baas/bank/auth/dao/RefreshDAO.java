package com.baas.bank.auth.dao;

import com.baas.bank.auth.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshDAO {
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean save(RefreshToken refreshToken) {
        try {
            String key = "refresh_token:" + refreshToken.getUserId();
            // TTL 적용: 만료까지 남은 시간을 계산해 키에 직접 적용
            long now = System.currentTimeMillis();
            long expireAt = Long.MAX_VALUE;
            try {
                expireAt = Long.parseLong(refreshToken.getExpiration());
            } catch (NumberFormatException ignore) { /* expiration을 문자열로 보관 중인 구조 보완 */ }

            long ttlMs = expireAt-now;
            System.out.println(ttlMs);
            if (ttlMs > 0) {
                redisTemplate.opsForValue().set(key, refreshToken.getRefreshToken(), java.time.Duration.ofMillis(ttlMs));
            } else {
                // 만료 시간이 파싱되지 않거나 이미 지난 경우, 기본 24시간 TTL 적용
                redisTemplate.opsForValue().set(key, refreshToken.getRefreshToken(), java.time.Duration.ofDays(1));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<String> findByUserId(Long userId) {
        try {
            String key = "refresh_token:" + userId;
            Object token = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable((String) token);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existsByRefreshToken(String refreshToken) {
        try {
            // Redis에서 refresh token으로 검색 (비효율적이지만 간단한 구현)
            // 실제로는 refresh token을 key로 하는 구조가 더 효율적
            return redisTemplate.getConnectionFactory()
                    .getConnection()
                    .keys(("refresh_token:*").getBytes())
                    .stream()
                    .anyMatch(key -> {
                        Object token = redisTemplate.opsForValue().get(new String(key));
                        return refreshToken.equals(token);
                    });
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteByUserId(Long userId) {
        try {
            String key = "refresh_token:" + userId;
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteByRefreshToken(String refreshToken) {
        try {
            // refresh token으로 userId를 찾아서 삭제
            return redisTemplate.getConnectionFactory()
                    .getConnection()
                    .keys(("refresh_token:*").getBytes())
                    .stream()
                    .anyMatch(key -> {
                        Object token = redisTemplate.opsForValue().get(new String(key));
                        if (refreshToken.equals(token)) {
                            return Boolean.TRUE.equals(redisTemplate.delete(new String(key)));
                        }
                        return false;
                    });
        } catch (Exception e) {
            return false;
        }
    }
}
