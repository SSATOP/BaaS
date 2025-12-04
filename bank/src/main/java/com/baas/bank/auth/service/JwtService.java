package com.baas.bank.auth.service;

import com.baas.bank.auth.config.TokenProperties;
import com.baas.bank.auth.dao.RefreshDAO;
import com.baas.bank.auth.entity.RefreshToken;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JwtService {
    private final TokenProperties tokenProperties;
    private final RefreshDAO refreshDAO;

    @Transactional
    public boolean saveRefreshToken(Long userId, String refreshToken, long expiredMs) {
        long expireAtMs = System.currentTimeMillis() + expiredMs;
        RefreshToken entity = new RefreshToken(userId, refreshToken, String.valueOf(expireAtMs));
        return refreshDAO.save(entity);
    }

    public Cookie createRefreshCookie(String token) {
        Cookie cookie = new Cookie(tokenProperties.getRefreshToken().getName(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        cookie.setSecure(false); // HTTP 환경에서 필수
        return cookie;
    }
}
