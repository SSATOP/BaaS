package com.baas.bank.auth.filter;

import com.baas.bank.user.dto.UserDto;
import com.baas.bank.auth.config.TokenProperties;
import com.baas.bank.auth.dao.RefreshDAO;
import com.baas.bank.user.dao.UserDAO;
import com.baas.bank.auth.exception.JwtAuthException;
import com.baas.bank.auth.provider.JwtProvider;
import com.baas.bank.auth.security.CustomUserDetails;
import com.baas.bank.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.baas.bank.auth.exception.JwtAuthException.*;
@Slf4j
@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final TokenProperties tokenProperties;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;

    private final RefreshDAO refreshDAO;
    private final UserDAO userDAO;

    /**
     * POST /login 클라이언트가 로그인 POST 요청을 보낼 때 실행
     *
     * @return AuthenticationManager 내부에서  UserDetailsService를 통해 검사 -> 결과에 따라 성공, 실패 메서드 호출
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 요청 바디 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);
            System.out.println(loginData);
            String loginId = loginData.get("loginId");
            String loginPassword = loginData.get("loginPassword");
            if (loginId == null || loginPassword == null) {
                request.setAttribute("exception", EMPTY_EMAIL_OR_PASSWORD);
                throw new JwtAuthException(EMPTY_EMAIL_OR_PASSWORD);
            }

            Optional<UserDto> findUser = userDAO.findByLoginId(loginId);
            if (findUser.isEmpty()) {
                request.setAttribute("exception", INVALID_EMAIL_OR_PASSWORD);
                throw new JwtAuthException(INVALID_EMAIL_OR_PASSWORD);
            }
            UserDto user = findUser.get();
            log.info(user.getLoginPassword());


            //스프링 시큐리티에서 userId와 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, loginPassword, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            request.setAttribute("exception", UNSUPPORTED_TYPE);
            throw new JwtAuthException(UNSUPPORTED_TYPE, e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        //유저 정보 - CustomUserDetails에서 userId 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        // HACK: role 값 사용시 사용
        // Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        // GrantedAuthority auth = iterator.next();
        // String role = auth.getAuthority();

        // 기존 refresh 토큰 삭제
        refreshDAO.deleteByUserId(userId);

        //토큰 생성
        TokenProperties.TokenConfig accessConfig = tokenProperties.getAccessToken();
        TokenProperties.TokenConfig refreshConfig = tokenProperties.getRefreshToken();

        String access = jwtProvider.createJwt(accessConfig.getName(), userId, accessConfig.getExpiry());
        String refresh = jwtProvider.createJwt(refreshConfig.getName(), userId, refreshConfig.getExpiry());

        response.setHeader("Authorization", "Bearer " + access);
        Cookie refreshCookie = jwtService.createRefreshCookie(refresh);
        response.addCookie(refreshCookie);

        boolean isSaved = jwtService.saveRefreshToken(userId, refresh, refreshConfig.getExpiry());
        if (!isSaved) {
            request.setAttribute("exception", DB_ERROR);
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        if (failed instanceof JwtAuthException e) {
            throw e;
        } else if (failed instanceof BadCredentialsException) {
            request.setAttribute("exception", INVALID_EMAIL_OR_PASSWORD);
            throw new JwtAuthException(INVALID_EMAIL_OR_PASSWORD, failed);
        } else {
            throw new JwtAuthException(UNKNOWN_ERROR);
        }
    }
}
