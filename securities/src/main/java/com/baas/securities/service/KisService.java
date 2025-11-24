package com.baas.securities.service;

import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.InternalServerErrorException;
import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.util.WebSocketMessageMaker;
import com.baas.securities.ws.KisWebSocketHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.beans.factory.ObjectProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class KisService {
    /**
     * TODO : [SEC_10]approval key 는 1일 마다 갱신해야함 -> 갱신해서 따로 처리하는 로직 필요. (ex: 스케줄러, 캐시 등을 사용하여 approval key를 받아옴)
     */

    private final ObjectProvider<KisWebSocketHandler> kisWebSocketHandlerProvider;

    private final KisSocketRepository kisRepository;
    private final WebSocketMessageMaker messageMaker;
    private final RestTemplate restTemplate = new RestTemplate();

    // 현재 구동죽인 종목들 기억하는 저장소
    private final Set<String> subscribedTickers = Collections.newSetFromMap(new ConcurrentHashMap<>());


    @Value("${APP_KEY}")
    private String appKey;

    @Value("${APP_SECRET}")
    private String appSecret;
    // 1. 키 발급용 HTTP URL
    private static final String KIS_API_URL = "https://openapivts.koreainvestment.com:29443/oauth2/Approval";
    // 실전투자인 경우: "https://openapi.koreainvestment.com:9443/oauth2/Approval"



    // 2. 실시간 통신용 WebSocket URL
    private static final String KIS_WS_URL = "ws://ops.koreainvestment.com:31000";
    // 실전투자의 경우: ws://ops.koreainvestment.com:21000

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
            subscribedTickers.add(ticker);
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
            subscribedTickers.remove(ticker);
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

            // 요청 바디 생성
            Map<String, String> body = Map.of(
                    "grant_type", "client_credentials",
                    "appkey", appKey,
                    "secretkey", appSecret
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // POST 요청 보내기
            Map response = restTemplate.postForObject(KIS_API_URL, request, Map.class);

            if (response != null && response.containsKey("approval_key")) {
                String newKey = (String) response.get("approval_key");
                log.info("새 승인 키 발급 성공");
                return newKey;
            } else {
                throw new RuntimeException("API 응답에 approval_key가 없습니다.");
            }

        } catch (Exception e) {

            log.error("KIS 승인 키 발급 API 호출 실패", e);
            throw new InternalServerErrorException(ErrorCode.MARKET_DATA_ERROR);
        }
    }

    /**
     * [추가] 웹소켓 재연결 및 구독 복구 로직
     * 새 키가 발급된 후 호출되어야 합니다.
     */
    public void refreshWebSocketConnection() {
        log.info("웹소켓 재연결 및 구독 복구 로직 시작 (대상 종목 수: {})", subscribedTickers.size());

        try {
            // 1. 기존 연결 끊기 (Repository disconnect 호출)
            log.info("기존 연결 종료 시도...");
            kisRepository.disconnect();

            // 2. 잠시 대기 (소켓이 완전히 닫히고 리소스가 정리될 시간)
            Thread.sleep(1000);

            // 3. 새 연결 맺기
            log.info("새로운 키로 KIS 웹소켓 연결 시도 중... URL: {}", KIS_WS_URL);

            WebSocketClient client = new StandardWebSocketClient();

            KisWebSocketHandler handler = kisWebSocketHandlerProvider.getObject();

            client.execute(handler, KIS_WS_URL).get();

            log.info("KIS 웹소켓 재연결 및 핸드쉐이크 성공!");

        } catch (Exception e) {
            log.error("웹소켓 재연결 실패! (잠시 후 다시 시도하거나 관리자 확인 필요)", e);
        }
    }

}
