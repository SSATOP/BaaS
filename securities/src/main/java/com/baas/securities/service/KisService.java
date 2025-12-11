package com.baas.securities.service;

import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.InternalServerErrorException;
import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.util.WebSocketMessageMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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



    /**
     *
     * KIS 서버에 실제 '구독' 메시지를 전송
     * @param ticker 종목 코드
     */
    public void subscribeToStock(String ticker) throws IOException {
        String approvalKey = kisRepository.getApprovalKey();

        // (중요) messageMaker에 buildSubRequest가 있어야 합니다.
        String reqMsg = messageMaker.buildSubRequest(ticker, approvalKey);

        log.info("KIS 구독 메시지 전송: {}, {}", ticker, reqMsg);

        try {
            kisRepository.getKisSession()
                    .orElseThrow(() -> new InternalServerErrorException(ErrorCode.STREAM_ERROR))
                    .sendMessage(new TextMessage(reqMsg));

        } catch (IOException e) {

            log.error("KIS WebSocket 메시지 전송 실패 (구독 해지): {}", ticker, e);
            throw new InternalServerErrorException(ErrorCode.STREAM_ERROR);
        }

        log.info("KIS 구독 요청 완료: {}", ticker);
    }

    /**
     * KIS 서버에 실제 '구독 해지' 메시지를 전송합니다.
     * @param ticker 종목 코드
     */
    public void unsubscribeFromStock(String ticker) throws IOException {
        String approvalKey = kisRepository.getApprovalKey();

        // (중요) messageMaker에 '구독 해지' 메시지를 만드는 메소드가 필요합니다.
        //       (이름은 예시입니다: buildUnsubRequest)
        String reqMsg = messageMaker.buildUnsubRequest(ticker, approvalKey);

        log.info("KIS 구독 해지 메시지 전송: {}, {}", ticker, reqMsg);

        try {
            kisRepository.getKisSession()

                    .orElseThrow(() -> new InternalServerErrorException(ErrorCode.STREAM_ERROR))
                    .sendMessage(new TextMessage(reqMsg));

        } catch (IOException e) {

            log.error("KIS WebSocket 메시지 전송 실패 (구독 해지): {}", ticker, e);
            throw new InternalServerErrorException(ErrorCode.STREAM_ERROR);
        }
        log.info("KIS 구독 해지 요청 완료: {}", ticker);
    }

    /**
     * [KisScheduler가 호출]
     * KIS API를 직접 호출하여 새 Approval Key를 발급받아 반환합니다.
     * @return 새로 발급받은 KIS 승인 키 (String)
     * @throws Exception API 호출 실패 시
     */
    public String fetchNewApprovalKeyFromKisApi() throws Exception {
        log.info("KIS API에 새 승인 키 발급을 요청합니다...");

        try {
            // TODO: (이전 설명과 동일)
            // KIS '승인 키 발급' API 실제 호출 로직 구현...
            // (RestTemplate 또는 WebClient 사용)


            String temporaryNewKey = "temp_new_key_from_api_call_" + System.currentTimeMillis();
            log.info("임시 새 키 발급: {}", temporaryNewKey);
            return temporaryNewKey;

        } catch (Exception e) {

            log.error("KIS 승인 키 발급 API 호출 실패", e);
            throw new InternalServerErrorException(ErrorCode.MARKET_DATA_ERROR);
        }
    }

}
