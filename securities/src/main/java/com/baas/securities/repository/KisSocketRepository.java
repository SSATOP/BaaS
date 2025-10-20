package com.baas.securities.repository;

import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

public interface KisSocketRepository {
    void subTicker(String ticker, String sessionId);

    boolean haveSubscriber(String ticker);

    int unsubTicker(String ticker, String sessionId);

    void setKisSession(WebSocketSession session);

    void removeKisSession(WebSocketSession session);

    Optional<WebSocketSession> getKisSession();

    Optional<String> findTickerBySessionId(String sessionId);
}
