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

    // 💡 1. NOT_NEED_VALID 목록 통합 및 수정
    private final List<String> NOT_NEED_VALID = List.of(
            "/login",
            "/ws-stomp",
            "/favicon.ico",
            "/error",
            "/stock/period",
            "/oauth2" // OAuth2 시작/콜백 경로는 필터 검증에서 제외하는 것이 일반적
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 1. 인증이 필요 없는 URL이면 통과
        if (invalidURL(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 💡 2. authentication 변수를 try 블록 바깥에 선언하고 null로 초기화합니다.
        //     (UsernamePasswordAuthenticationToken은 인증이 없을 때 null로 초기화)
        UsernamePasswordAuthenticationToken authentication = null;
        String email = null; // email 변수도 범위 확장

        try {
            String token = extractTokenFromRequest(request);

            // 2. [수정됨] 토큰이 있을 때만 검증 로직 수행
            if (token != null) {
                email = jwtHandler.resolve(token);

                authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("login email={}", email);
            }
            

        } catch (UnauthorizedException e) {
            // 토큰이 위조되었거나 만료된 경우 여기서 잡힘
            // RuntimeException 대신 커스텀 예외 처리를 하는 것이 좋습니다. (현재는 그대로 둠)
            throw new RuntimeException(e);
        }

        // 💡 3. 필터 체인 진행 로직을 finally 블록에만 남겨서 한 번만 실행되도록 합니다.
        finally {
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