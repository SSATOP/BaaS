package com.baas.securities.ws;

import com.baas.securities.dto.security.JwtDto;
import com.baas.securities.security.util.JwtHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtHandler jwtHandler;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공! 토큰 생성 로직 실행");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. 이메일 추출
        Map<String, Object> responseMap = (Map<String, Object>) attributes.get("response");

        if (responseMap == null) {
            responseMap = attributes;
        }
        String email = (String) responseMap.get("email");
        if (email == null) {
            log.error("치명적 오류: OAuth2User attributes에 이메일이 없습니다. attributes={}", attributes);
            throw new RuntimeException("이메일 정보를 찾을 수 없습니다.");
        }

        log.info("JWT 발급 대상 이메일: {}", email);

        // 2. JWT 토큰 생성
        JwtDto jwtTokens = jwtHandler.generate(email);

        // ==========================================
        // [추가된 로직] 3. Refresh Token 쿠키 설정
        // ==========================================
        // 친구 코드의 jwtService.createRefreshCookie() 역할을 여기서 직접 수행합니다.
        Cookie refreshCookie = new Cookie("refresh_token", jwtTokens.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);
        // ==========================================

        // 4. Access Token을 JSON 응답 본문에 담아 전송
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        // ObjectMapper를 사용하여 JwtDto를 JSON 문자열로 변환하여 응답
        // (주의: ObjectMapper 의존성이 필요할 수 있습니다. 여기서는 임시로 Map을 사용)
        String jsonResponse = String.format("{\"accessToken\": \"%s\"}", jwtTokens.getAccessToken());

        // 실제로는 ObjectMapper를 사용해야 합니다. (예시)
        /*
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(jwtTokens);
        */

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}