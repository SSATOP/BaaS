package com.baas.securities.repository;

import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.Set;

/**
 * redis 같은 캐시 메모리 사용 필요
 * approval key 만료 기한 1일 따라서 23시간 마다 갱신 로직 필요.
 */

public interface KisSocketRepository {
    int subTicker(String ticker, String sessionId);

    boolean haveSubscriber(String ticker);

    int unsubTicker(String ticker, String sessionId);

    void setKisSession(WebSocketSession session);

    void removeKisSession(WebSocketSession session);

    Optional<WebSocketSession> getKisSession();

    Optional<String> findTickerBySessionId(String sessionId);

    boolean isStompSessionId(String sessionId);

    Set<String> getTickers();

    String getApprovalKey();

    void updateApprovalKey(String newApprovalKey);
    // 소켓 강제로 끊는 메소드 추가
    void disconnect();
}
