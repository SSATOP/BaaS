package com.baas.securities.ws;

import com.baas.securities.enums.CustomStompCommand;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.UnauthorizedException;
import com.baas.securities.security.util.JwtHandler;
import com.baas.securities.util.StompHeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.baas.securities.util.StompHeaderUtil.USER_AUTHENTICATION_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE) // 가장 높은 우선순위 설정.
public class AuthStompHandler implements ChannelInterceptor {

    private final JwtHandler jwtHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // stomp header에 접근하기 위한 처리
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 로그인이 필요하지 않는 서비스 ( 실시간 시세 받기 ) 판별을 위한 attributes 접근
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        // dontNeedAuth == /ws-stomp 경로 ( 실시간 시세 ) 일 경우 attribute가 들어옴.
        if (Objects.requireNonNull(sessionAttributes).containsKey(StompHeaderUtil.DONTNEEDAUTH)) {
            log.info("session attribute={}, value={}", StompHeaderUtil.DONTNEEDAUTH, sessionAttributes.get(StompHeaderUtil.DONTNEEDAUTH));
            return ChannelInterceptor.super.preSend(message, channel);
        }

        if (Objects.isNull(accessor.getCommand())) {
            log.info("command is null");
            return ChannelInterceptor.super.preSend(message, channel);
        }

        // 그 외 : ( 매수 / 매도 ) 주문 웹소켓 연결
        try {
            if (CustomStompCommand.isConnectCommand(accessor.getCommand().name())) {
                log.info("connect request");
                validateAuth(accessor, sessionAttributes);
                log.info("auth 인증 완료.");
            }

            if (CustomStompCommand.isSendOrSubscribe(accessor.getCommand().name())) {
                setAuthUser(accessor, sessionAttributes);
                log.info("user 설정 완료, command={}", accessor.getCommand().name());
            }
        } catch (IllegalAccessException e) {
            // 예외 처리 필요.
            throw new RuntimeException(e);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private void validateAuth(StompHeaderAccessor accessor, Map<String, Object> sessionAttributes) throws IllegalAccessException {
        String authorization = accessor.getFirstNativeHeader(StompHeaderUtil.AUTHORIZATION);

        String token = extractToken(authorization);

        String email = jwtHandler.resolve(token);
        log.info("user email={}", email);
        // 나중에 서비스에 맞는 authentication으로 변경필요.

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        accessor.setUser(authentication);

        // connect 커맨드 말고도, send, subscribe에서 사용자 정보를 사용하기 위해 세션에 저장.
        sessionAttributes.put(USER_AUTHENTICATION_KEY, authentication);
    }

    // send와 subscribe 커맨드에서 connect 때 세션에 저장한 사용자 정보를 가져와야 함.
    private void setAuthUser(StompHeaderAccessor accessor, Map<String, Object> sessionAttributes) {
        Authentication authentication = (Authentication) sessionAttributes.get(USER_AUTHENTICATION_KEY);

        accessor.setUser(authentication);
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)|| !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_TOKEN);
        }
        return authorization.substring(7);
    }
}