package com.baas.bank.auth.oauth2;

import com.baas.bank.auth.config.TokenProperties;
import com.baas.bank.auth.provider.JwtProvider;
import com.baas.bank.auth.service.JwtService;
import com.baas.bank.user.dao.UserDAO;
import com.baas.bank.user.dto.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.baas.bank.auth.exception.JwtAuthException.DB_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProperties tokenProperties;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;
    private final UserDAO userDAO;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // DB 저장 or 조회
        Optional<UserDto> dto = userDAO.findById(oAuth2User.getId());
        UserDto user = dto.orElseThrow(() -> new IllegalArgumentException("User not found"));

        //토큰 생성
        TokenProperties.TokenConfig accessConfig = tokenProperties.getAccessToken();
        TokenProperties.TokenConfig refreshConfig = tokenProperties.getRefreshToken();

        String access = jwtProvider.createJwt(accessConfig.getName(), user.getId(), accessConfig.getExpiry());
        String refresh = jwtProvider.createJwt(refreshConfig.getName(), user.getId(), refreshConfig.getExpiry());

        boolean isSaved = jwtService.saveRefreshToken(user.getId(), refresh, refreshConfig.getExpiry());
        if (!isSaved) {
            request.setAttribute("exception", DB_ERROR);
        }

        // 쿠키 저장 or 헤더 응답
        response.setHeader("Authorization", "Bearer " + access);
        Cookie refreshCookie = jwtService.createRefreshCookie(refresh);
        response.addCookie(refreshCookie);

        response.sendRedirect("/login/success");
    }
}