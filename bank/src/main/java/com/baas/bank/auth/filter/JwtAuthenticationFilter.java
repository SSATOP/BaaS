package com.baas.bank.auth.filter;

import com.baas.bank.account.dto.UserDto;
import com.baas.bank.auth.config.TokenProperties;
import com.baas.bank.auth.dao.UserDAO;
import com.baas.bank.auth.exception.JwtAuthException;
import com.baas.bank.auth.provider.JwtProvider;
import com.baas.bank.auth.security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.baas.bank.auth.exception.JwtAuthException.*;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProperties tokenProperties;
    private final JwtProvider jwtProvider;
    private final UserDAO userDAO;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = parseBearerToken(request);

        // 토큰이 없는 경우(ex 로그인 전 요청)는 인증 없이 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
            if (jwtProvider.isExpired(accessToken)) {
                throw new JwtAuthException(EXPIRED_TOKEN);
            }
            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtProvider.getCategory(accessToken);
            if (!category.equals(tokenProperties.getAccessToken().getName())) {
                throw new JwtAuthException(INVALID_TOKEN);
            }

            // HACK: role 값을 추가한다면 해당 코드에도 변경해야 함
            Long userId = jwtProvider.getUserId(accessToken);
            Optional<UserDto> findUser = userDAO.findById(userId);
            if (findUser.isEmpty()) {
                throw new JwtAuthException(INVALID_USER);
            }

            CustomUserDetails customUserDetails = new CustomUserDetails(findUser.orElse(null));

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            log.info("JWT token expired : ", e);
            request.setAttribute("exception", EXPIRED_TOKEN);
            throw new JwtAuthException(EXPIRED_TOKEN, e);
        } catch (JwtAuthException e) {
            log.info("JWT token invalid : ", e);
            request.setAttribute("exception", e.getErrorCode());
            throw e;
        } catch (Exception e) {
            log.info("JWT token error : ",e);
            request.setAttribute("exception", INVALID_TOKEN);
            throw new JwtAuthException(INVALID_TOKEN, e);

        }
        filterChain.doFilter(request, response);
    }


    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if (!isBearer) return null;

        return authorization.substring(7);

    }
}
