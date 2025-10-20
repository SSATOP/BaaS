package com.baas.securities.handler;

import com.baas.securities.enums.CustomStompCommand;
import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.util.WebSocketMessageMaker;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class StompPreHandler implements ChannelInterceptor {

    @Value("${KIS_WEBSOCKET_APPROVAL_KEY}")
    private String approvalKey;

    private final KisSocketRepository kisRepository;
    private final WebSocketMessageMaker messageMaker;

    /**
     * 메시지 보내기 전에 요청을 가로채는 interceptor
     * DISCONNECT나 UNSUBSCRIBE 때에 종목에 대한 구독을 끊기 위해
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        String sessionId = accessor.getSessionId();
        StompCommand command = accessor.getCommand();

        log.info("preSend: stomp session id={}, command={}", sessionId, command);

        /**
         * 구독이 끝났다면 repo에서 제거
         * 종목에 대한 구독 끝 메시지 KIS에 전달.
         */
        if (command != null && CustomStompCommand.isCloseCommand(command.name())) {
            String ticker = kisRepository.findTickerBySessionId(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("이미 닫힌 세션입니다."));
            int subscriber = kisRepository.unsubTicker(ticker, sessionId);
            try {
                unsubscribeStock(subscriber, ticker);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    private void unsubscribeStock(int subscriber, String ticker) throws IOException {
        if (subscriber > 0) {
            return;
        }
        /**
         * 만약 종목 구독자가 0명이라면 KIS 에게 종목 구독 해지 메시지 송신.
         */
        String reqMsg = messageMaker.buildUnsubRequest(ticker, approvalKey);
        kisRepository.getKisSession()
                .orElseThrow(() -> new IllegalArgumentException("웹소켓 세션을 찾지 못하였습니다."))
                .sendMessage(new TextMessage(reqMsg));
    }
}
