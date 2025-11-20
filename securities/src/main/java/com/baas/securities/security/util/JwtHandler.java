package com.baas.securities.security.util;

import com.baas.securities.dto.security.JwtDto;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtHandler {

    @Value("${JWT_ACCESS_EXPIRE_TIME}")
    private long accessExpire;
    @Value("${JWT_REFRESH_EXPIRE_TIME}")
    private long refreshExpire;
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public JwtDto generate(String email) {
        String accessToken = createToken(email, accessExpire);
        String refreshToken = createToken(email, refreshExpire);

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String resolve(String token) throws IllegalAccessException {
        validation(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // TODO : 예외 처리 필요.
    //모든 JWT 관련 예외를 하나의 catch 블록으로 통합
    // (SecurityException, MalformedJwtException, SignatureException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException 등)
    private void validation(String token) throws ExpiredJwtException, IllegalAccessException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            log.info("JWT VALIDATE SUCCESS");
        } // (SecurityException, MalformedJwtException, SignatureException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException 등)
        catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            // [수정] 모든 예외를 'UnauthorizedException'으로 변환하여 throw
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_TOKEN);
        }
        catch (NullPointerException e) {
            log.warn("JWT token is null");
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_TOKEN);
        }
    }

    private String createToken(String email, long time) {
        long now = (new Date()).getTime();
        Date date = new Date(now + time);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
