package com.baas.securities.handler;

import com.baas.securities.enums.CustomStompCommand;
import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.service.KisService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompPreHandler implements ChannelInterceptor {

    private final KisSocketRepository kisRepository;
    //private final WebSocketMessageMaker messageMaker;
    private final KisService kisService;
    /**
     * 메시지 보내기 전에 요청을 가로채는 interceptor
     * 1. SUBSCRIBE: 첫 구독자일 경우 KIS 구독 메시지 전송
     * 2. DISCONNECT/UNSUBSCRIBE: 마지막 구독자일 경우 KIS 구독 해지 메시지 전송
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        StompCommand command = accessor.getCommand();

        if (sessionId == null) {
            log.warn("StompPreHandler: 세션 ID가 없는 요청입니다.");
            return message;
        }

        log.info("preSend: stomp session id={}, command={}", sessionId, command);

        try {
            // ---  5. (신규) SUBSCRIBE 로직 추가  ---
            if (StompCommand.SUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                String ticker = parseStockCodeFromDestination(destination);

                if (ticker != null) {
                    log.info("[SUB] Session: {}, Ticker: {}", sessionId, ticker);


                    // Redis에 구독자로 추가하고 '현재 총 인원수'를 받음
                    int subscriberCount = kisRepository.subTicker(ticker, sessionId);

                    // '첫 번째 구독자'라면 (방금 내가 추가해서 1명이 됨)
                    if (subscriberCount == 1) {
                        log.info("[KIS] 첫 구독자 발생. KIS에 구독 메시지 전송: {}", ticker);
                        kisService.subscribeToStock(ticker);
                    }
                }
            }
            // ---  SUBSCRIBE 로직 끝  ---

            // ---  6. (기존) DISCONNECT / UNSUBSCRIBE 로직  ---
            else if (isStompUnsubMsg(sessionId, command)) {
                kisRepository.findTickerBySessionId(sessionId)
                        .ifPresent(ticker -> {
                            try {
                                log.info("[UNSUB/DIS] Session: {}, Ticker: {}", sessionId, ticker);
                                unsubscribeStock(ticker, sessionId);
                            } catch (IOException e) {
                                log.error("구독 해지 처리 중 IO 예외 발생", e);
                                // (필요시) throw new RuntimeException(e);
                            }
                        });
            }
        } catch (Exception e) {
            // (예외 처리)
            log.error("StompPreHandler 처리 중 예외 발생: {}", e.getMessage(), e);
        }


        return message;
    }

    private boolean isStompUnsubMsg(String sessionId, StompCommand command) {

        return kisRepository.isStompSessionId(sessionId) && command != null && CustomStompCommand.isCloseCommand(command.name());
    }

    private void unsubscribeStock(String ticker, String sessionId) throws IOException {

        int subscriber = kisRepository.unsubTicker(ticker, sessionId);

        if (subscriber > 0) {
            log.info("[KIS] 구독자 {}명 남음 (해지 안함): {}", subscriber, ticker);
            return;
        }

        /**
         * 만약 종목 구독자가 0명이라면 KIS 에게 종목 구독 해지 메시지 송신.
         */
        log.info("[KIS] 마지막 구독자 퇴장. KIS에 구독 해지 메시지 전송: {}", ticker);
        // (KisService의 메소드 호출로 변경)
        kisService.unsubscribeFromStock(ticker);

        /* (KisService가 이 로직을 대신 수행함)
        String approvalKey = kisRepository.getApprovalKey();
        String reqMsg = messageMaker.buildUnsubRequest(ticker, approvalKey);
        kisRepository.getKisSession()
                .orElseThrow(() -> new IllegalArgumentException("웹소켓 세션을 찾지 못하였습니다."))
                .sendMessage(new TextMessage(reqMsg));
        log.info("unsubscribe={}", ticker);
        */
    }

    /**
     * (신규) Destination 경로에서 종목 코드를 파싱합니다.
     * (예: "/sub/stock/005930" -> "005930" 반환)
     */
    private String parseStockCodeFromDestination(String destination) {
        if (destination == null || !destination.startsWith("/sub/stock/")) {
            return null;
        }
        try {
            String[] parts = destination.split("/");
            if (parts.length > 0) {
                return parts[parts.length - 1]; // 마지막 부분을 종목 코드로 간주
            }
        } catch (Exception e) {
            log.error("Destination 파싱 실패: {}", destination, e);
        }
        return null;
    }
}