package com.baas.securities.security.util;

import com.baas.securities.dto.JwtDto;
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

    private void validation(String token) throws ExpiredJwtException, IllegalAccessException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            log.info("JWT VALIDATE SUCCESS");
        } catch (SecurityException | MalformedJwtException e) {
            throw new IllegalAccessException("Invalid JWT Token %s".formatted(e.getMessage()));
        } catch (ExpiredJwtException e) {
            throw new IllegalAccessException("Expired JWT Token %s".formatted(e.getMessage()));
        } catch (UnsupportedJwtException e) {
            throw new IllegalAccessException("Unsupported JWT Token %s".formatted(e.getMessage()));
        } catch (IllegalArgumentException e) {
            throw new IllegalAccessException("JWT claims string is empty. %s".formatted(e.getMessage()));
        } catch (NullPointerException e) {
            throw new IllegalAccessException("NOT EXISTS TOKEN [%s]".formatted(e.getMessage()));
        } catch (io.jsonwebtoken.security.SignatureException e) {
            throw new IllegalAccessException("Signature Exception [%s]".formatted(e.getMessage()));
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
