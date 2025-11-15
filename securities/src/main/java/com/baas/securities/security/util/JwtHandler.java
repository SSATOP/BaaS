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

    public String resolve(String token) throws UnauthorizedException {
        validation(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // TODO : 예외 처리 필요.
    private void validation(String token) throws UnauthorizedException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            log.info("JWT VALIDATE SUCCESS");
        } catch (SecurityException | MalformedJwtException e) {
            throw new UnauthorizedException("Invalid JWT Token %s".formatted(e.getMessage()), "INVALID_403");
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Expired JWT Token %s".formatted(e.getMessage()), "EXPIRED_403");
        } catch (UnsupportedJwtException e) {
            throw new UnauthorizedException("Unsupported JWT Token %s".formatted(e.getMessage()), "UNSUPPORTED_403");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("JWT claims string is empty. %s".formatted(e.getMessage()), "STRING_EMPTY_403");
        } catch (NullPointerException e) {
            throw new UnauthorizedException("NOT EXISTS TOKEN [%s]".formatted(e.getMessage()), "NOT_EXISTS_403");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            throw new UnauthorizedException("Signature Exception [%s]".formatted(e.getMessage()), "SIGNATURE_EX_403");
        } // (SecurityException, MalformedJwtException, SignatureException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException 등)
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
