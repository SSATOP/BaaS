package com.baas.securities.exception;

import com.baas.securities.exception.ex.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    /**
     * AuthStompHandler에서 던져진 auth 에러를 처리하는 로직 존재.
     */
    private final ObjectMapper mapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        // AuthStompHandler에서 던져진 UnauthorizedException일 경우
        if (ex instanceof MessageDeliveryException) {
            Throwable cause = ex.getCause();

            if (cause instanceof UnauthorizedException) {
                UnauthorizedException exception = (UnauthorizedException) cause;
                log.info("cause jwt exception: {}", cause.getMessage());
                return sendErrorMessage(new ErrorResponse(1003, exception.getCode(), exception.getMessage(), LocalDateTime.now()));
            }
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> sendErrorMessage(ErrorResponse errorResponse) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
        headers.setMessage(errorResponse.message());
        headers.setLeaveMutable(true);

        try {
            String json = mapper.writeValueAsString(errorResponse);
            return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8),
                    headers.getMessageHeaders());
        } catch (JsonProcessingException e) {
            log.error("Failed to convert ErrorResponse to JSON", e);
            return MessageBuilder.createMessage(errorResponse.message().getBytes(StandardCharsets.UTF_8),
                    headers.getMessageHeaders());
        }
    }
}
