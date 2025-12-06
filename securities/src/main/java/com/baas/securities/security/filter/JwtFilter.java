package com.baas.securities.security.filter;

import com.baas.securities.exception.ex.UnauthorizedException;
import com.baas.securities.security.util.JwtHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtHandler jwtHandler;
    // ws-stomp 등 인증이 필요 없는 경로는 여기에 추가
    private final List<String> NOT_NEED_VALID = List.of("/login", "/ws-stomp", "/favicon.ico", "/error");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 1. 인증이 필요 없는 URL이면 통과
        if (invalidURL(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromRequest(request);

            // 2. [수정됨] 토큰이 있을 때만 검증 로직 수행
            if (token != null) {
                String email = jwtHandler.resolve(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("login email={}", email);
            }
            // 토큰이 없으면 그냥 아래 finally 블록으로 넘어가서 다음 필터 실행 (로그인 안 된 상태로 접속 시도)

        } catch (UnauthorizedException e) {
            // 토큰이 위조되었거나 만료된 경우 여기서 잡힘
            throw new RuntimeException(e);
        } finally {
            // 3. 필터 체인 계속 진행 (가장 중요)
            filterChain.doFilter(request, response);
        }
    }

    private boolean invalidURL(String requestURI) {
        return NOT_NEED_VALID.stream().anyMatch(requestURI::contains);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        // [수정됨] 변수명을 authorization으로 통일했습니다.
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}