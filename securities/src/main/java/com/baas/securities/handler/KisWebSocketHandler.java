package com.baas.securities.handler;

import com.baas.securities.dto.stock.RealtimeStockDTO;
import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.util.WebSocketMessageMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class KisWebSocketHandler extends TextWebSocketHandler {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final KisSocketRepository kisRepository;
    private final WebSocketMessageMaker messageMaker;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String kisSessionId = session.getId();
        kisRepository.setKisSession(session);

        recoverKISSub(session);

        log.info("socket connection established, session id={}", kisSessionId);
    }

    /**
     *  KIS 웹 소켓으로 부터 메시지를 받을때 실행되는 메소드
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
//        log.info("receive text message={}", msg);

        /**
         * KIS 서버로부터 핑퐁 메시지를 받을때, pingpong 메시지를 kis 웹소켓 서버에게 송신
         */
        if (isPingPongMsg(msg)) {
            String pingPongMsg = messageMaker.makePingPongMsg();
            session.sendMessage(new TextMessage(pingPongMsg));
            log.info("send ping pong msg in KisWebSocketHandler.handleTextMessage, session id={}", session.getId());
            return;
        }

        /**
         * KIS 서버의 응답 메시지 파싱
         */
        String[] stockInfo = msg.split("\\^");

        if (stockInfo.length > 1) {
            // 한줄에 다수의 데이터 처리
            List<RealtimeStockDTO> dtos = getResMessages(stockInfo);
            for (RealtimeStockDTO dto : dtos) {
                simpMessagingTemplate.convertAndSend("/sub/stock/" + dto.getTicker(), dto);
//                log.info("dest: {}, send message: {}", "/sub/stock/" + dto.getTicker(), dto.getTicker());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        session.close();
        kisRepository.removeKisSession(session);

        log.info("afterConnectionClosed {}", session.getId());
        log.info("close reason={}", status.getReason());
    }

    private void recoverKISSub(WebSocketSession session) throws IOException {
        List<String> recovered = new ArrayList<>();

        String approvalKey = kisRepository.getApprovalKey();
        for (String ticker : kisRepository.getTickers()) {
            if (kisRepository.haveSubscriber(ticker)) {

                String msg = messageMaker.buildSubRequest(ticker, approvalKey);
                session.sendMessage(new TextMessage(msg));

                recovered.add(ticker);
            }
        }

        if (!approvalKey.isEmpty()) {
            log.info("recovered ticker subs={}", recovered);
        }
    }

    private List<RealtimeStockDTO> getResMessages(String[] stockInfo) {
        List<RealtimeStockDTO> dtos = new ArrayList<>();

        final int INTERVAL_NUM = 46;
        int startIdx = 0;

        while (startIdx < stockInfo.length) {
            RealtimeStockDTO dto = generateRealtimeStockDTO(stockInfo, startIdx);
            dtos.add(dto);

            startIdx += INTERVAL_NUM;
        }

        return dtos;
    }


    private RealtimeStockDTO generateRealtimeStockDTO(String[] stockInfo, int startIdx) {
        return RealtimeStockDTO.builder()
                .ticker(extractTicker(stockInfo[startIdx]))
                .tradeTime(formatTradeTime(stockInfo[startIdx + 1]))
                .price(Double.parseDouble(stockInfo[startIdx + 2]))
                .change(new BigInteger(stockInfo[startIdx + 4]))
                .changeRate(new BigDecimal(stockInfo[startIdx + 5]))
                .tradeVolume(new BigInteger(stockInfo[startIdx + 12]))
                .accTradeVolume(new BigInteger(stockInfo[startIdx + 13]))
                .accTradeValue(new BigInteger(stockInfo[startIdx + 14]))
                .openPrice(Double.parseDouble(stockInfo[startIdx + 7]))
                .highPrice(Double.parseDouble(stockInfo[startIdx + 8]))
                .lowPrice(Double.parseDouble(stockInfo[startIdx + 9]))
                .build();
    }

    private String extractTicker(String info) {
        if (!info.contains("|")) {
            return info;
        }
        String[] infos = info.split("\\|");
        return infos[3];
    }

    private String formatTradeTime(String time) {
        return "%s:%s:%s".formatted(time.substring(0, 2), time.substring(2, 4), time.substring(4, 6));
    }

    private boolean isPingPongMsg(String msg) {
        return msg.contains("PINGPONG");
    }
}
