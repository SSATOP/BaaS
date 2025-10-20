package com.baas.securities.repository.impl;

import com.baas.securities.repository.KisSocketRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class KisSocketRepositoryImpl implements KisSocketRepository {

    /**
     * K : 세션 아이디 (스톰프 구독자 세션 아이디), V : 종목명 (Ticker)
     */
    private final Map<String, String> users = new ConcurrentHashMap<>();
    /**
     * K : 종목명 (Ticker), V : 세션 아이디 리스트 (스톰프 구독자 세션 아이디)
     */
    private final Map<String, CopyOnWriteArrayList<String>> store = new ConcurrentHashMap<>();

    private final Map<String, WebSocketSession> sessionStore = new ConcurrentHashMap<>();

    @Override
    public void subTicker(String ticker, String sessionId) {
//        if (!store.containsKey(ticker)) {
//            store.put(ticker, new CopyOnWriteArrayList<>());
//        }
//        store.get(ticker).add(sessionId);
        /**
         * 동시성 처리에 안전.
         * 위 코드는 race condition이 발생할 수 있음.
         */
        store.computeIfAbsent(ticker, k -> new CopyOnWriteArrayList<>()).add(sessionId);
        users.put(ticker, sessionId);
    }

    @Override
    public boolean haveSubscriber(String ticker) {
        return store.get(ticker).isEmpty();
    }

    @Override
    public int unsubTicker(String ticker, String sessionId) {
        store.get(ticker).remove(sessionId);
        users.remove(sessionId);
        return store.get(ticker).size();
    }

    @Override
    public void setKisSession(WebSocketSession session) {
        sessionStore.put("session", session);
    }

    @Override
    public void removeKisSession(WebSocketSession session) {
        sessionStore.remove(session.getId());
    }

    @Override
    public Optional<WebSocketSession> getKisSession() {
        return Optional.of(sessionStore.getOrDefault("session", null));
    }

    @Override
    public Optional<String> findTickerBySessionId(String sessionId) {
        return Optional.of(users.getOrDefault(sessionId, null));
    }
}
