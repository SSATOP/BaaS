package com.baas.securities.handler;

import com.baas.securities.enums.CustomStompCommand;
import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.util.WebSocketMessageMaker;
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

    private final KisSocketRepository kisRepository;
    private final WebSocketMessageMaker messageMaker;

    /**
     * 메시지 보내기 전에 요청을 가로채는 interceptor
     * 실시간 주식 정보 수신 시에 stomp command가 DISCONNECT나 UNSUBSCRIBE 일때 종목에 대한 KIS 웹소켓 구독 해지
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
        if (isStompUnsubMsg(sessionId, command)) {
            kisRepository.findTickerBySessionId(sessionId)
                    .ifPresent(ticker -> {
                        try {
                            unsubscribeStock(ticker, sessionId);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });



        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    private boolean isStompUnsubMsg(String sessionId, StompCommand command) {
        return kisRepository.isStompSessionId(sessionId) && command != null && CustomStompCommand.isCloseCommand(command.name());
    }

    private void unsubscribeStock(String ticker, String sessionId) throws IOException {
        int subscriber = kisRepository.unsubTicker(ticker, sessionId);

        if (subscriber > 0) {
            return;
        }
        /**
         * 만약 종목 구독자가 0명이라면 KIS 에게 종목 구독 해지 메시지 송신.
         */
        String approvalKey = kisRepository.getApprovalKey();

        String reqMsg = messageMaker.buildUnsubRequest(ticker, approvalKey);
        kisRepository.getKisSession()
                .orElseThrow(() -> new IllegalArgumentException("웹소켓 세션을 찾지 못하였습니다."))
                .sendMessage(new TextMessage(reqMsg));
        log.info("unsubscribe={}", ticker);
    }
}
