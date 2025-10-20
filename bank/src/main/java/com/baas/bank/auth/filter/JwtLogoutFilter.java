package com.baas.bank.auth.filter;

import com.baas.bank.auth.config.TokenProperties;
import com.baas.bank.auth.dao.RefreshDAO;
import com.baas.bank.auth.exception.JwtAuthException;
import com.baas.bank.auth.provider.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

import static com.baas.bank.auth.exception.JwtAuthException.*;

@Slf4j
@RequiredArgsConstructor
public class JwtLogoutFilter extends GenericFilter {

    private final TokenProperties tokenProperties;

    private final JwtProvider jwtProvider;
    private final RefreshDAO refreshDAO;
    private static final String LOGOUT_URI = "/logout";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        handleLogout(request, response);
    }

    /**
     * 현재 요청이 로그아웃 요청인지 여부를 확인하는 메서드
     */
    private boolean isLogoutRequest(HttpServletRequest request) {
        return LOGOUT_URI.equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookies(request);
        try {
            if (refreshToken == null || !isValidRefreshToken(refreshToken)) {
                request.setAttribute("exception", TOKEN_NOT_FOUND);
                throw new JwtAuthException(TOKEN_NOT_FOUND);
            }
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", INVALID_TOKEN);
            throw new JwtAuthException(INVALID_TOKEN, e);
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", EXPIRED_TOKEN);
            throw new JwtAuthException(EXPIRED_TOKEN, e);
        }

        // 로그아웃 처리
        refreshDAO.deleteByRefreshToken(refreshToken);
        removeRefreshCookie(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Cookie 중 refresh 이름의 토큰을 찾아 반환
     */
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> tokenProperties.getRefreshToken().getName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 주어진 refresh 토큰이 정상적인지 확인
     */
    private boolean isValidRefreshToken(String refresh) {
        jwtProvider.isExpired(refresh);

        return tokenProperties.getRefreshToken().getName().equals(jwtProvider.getCategory(refresh)) &&
                refreshDAO.existsByRefreshToken(refresh);
    }

    private void removeRefreshCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie(tokenProperties.getRefreshToken().getName(), null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);
    }
}
