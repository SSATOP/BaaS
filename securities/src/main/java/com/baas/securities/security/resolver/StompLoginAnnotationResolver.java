package com.baas.securities.security.resolver;

import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.util.StompHeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MessageMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class StompLoginAnnotationResolver extends MessageMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean assignableFrom = AuthUser.class.isAssignableFrom(parameter.getParameterType());
        return hasParameterAnnotation && assignableFrom;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        Authentication userAuthentication = (Authentication) Objects.requireNonNull(sessionAttributes).get(StompHeaderUtil.USER_AUTHENTICATION_KEY);

        log.info("user in stomp={}", userAuthentication.getPrincipal());

        return AuthUser.builder()
                .email((String) userAuthentication.getPrincipal())
                .build();
    }
}
