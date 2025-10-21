package com.baas.securities.service;

import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.util.WebSocketMessageMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KisService {
    /**
     * TODO : [SEC_10]approval key 는 1일 마다 갱신해야함 -> 갱신해서 따로 처리하는 로직 필요. (ex: 스케줄러, 캐시 등을 사용하여 approval key를 받아옴)
     */
    private final KisSocketRepository kisRepository;
    private final WebSocketMessageMaker messageMaker;

    public void subRealtimeStock(String sessionId, String ticker) throws IOException {
        /**
         * 종목에 대한 구독이 없으면 KIS 서버에 구독 메시지 전송
         */
        log.info("ticker: {}, have subscriber={}", ticker, kisRepository.haveSubscriber(ticker));

        if (!kisRepository.haveSubscriber(ticker)) {
            sendRequestMsg(ticker);
        }
        /**
         * 종목에 대한 구독 세션 아이디 저장
         */
        kisRepository.subTicker(ticker, sessionId);
    }

    /**
     * unsubRealtimeStock(구독해지)은 Interceptor에서 Repository로 진행
     */

    private void sendRequestMsg(String ticker) throws IOException {
        // TODO : Session Error Handling
        String approvalKey = kisRepository.getApprovalKey();
        String reqMsg = messageMaker.buildSubRequest(ticker, approvalKey);

        log.info("sub msg to {}, {}", ticker, reqMsg);

        kisRepository.getKisSession()
                .orElseThrow(() -> new IllegalArgumentException("웹소켓이 연결되어있지 않습니다."))
                .sendMessage(new TextMessage(reqMsg));

        log.info("subscribe to {}", ticker);
    }
}
