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
    private final List<String> NOT_NEED_VALID = List.of("/login", "/ws-stomp", "/stock/period");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (invalidURL(requestURI)) {
            filterChain.doFilter(request, response);
            log.info("not need valid={}", requestURI);
            return;
        }

        try {
            String token = extractTokenFromRequest(request);
            String email = jwtHandler.resolve(token);

            // Spring Security에 인증 정보 설정
            // TODO : AuthenticationManager 설정 추가 필요.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("login email={}", email);
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invalidURL(String requestURI) {
        return NOT_NEED_VALID.stream().anyMatch(requestURI::contains);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        return authorization.substring(7);
    }
}
