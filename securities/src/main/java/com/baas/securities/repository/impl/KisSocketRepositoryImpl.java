package com.baas.securities.repository.impl;

import com.baas.securities.repository.KisSocketRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Slf4j
public class KisSocketRepositoryImpl implements KisSocketRepository {

    @Value("${KIS_WEBSOCKET_APPROVAL_KEY}")
    private String approvalKey;
    /**
     * K : 세션 아이디 (스톰프 구독자 세션 아이디), V : 종목명 (Ticker)
     */
    private final Map<String, String> users = new ConcurrentHashMap<>();
    /**
     * K : 종목명 (Ticker), V : 세션 아이디 리스트 (스톰프 구독자 세션 아이디)
     */
    private final Map<String, CopyOnWriteArrayList<String>> store = new ConcurrentHashMap<>();

    private final Map<String, Object> infos = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        infos.put("approvalKey", approvalKey);
    }

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
        users.put(sessionId, ticker);
        logging(ticker, sessionId);
    }

    @Override
    public boolean haveSubscriber(String ticker) {
        return store.containsKey(ticker) && !store.get(ticker).isEmpty();
    }

    @Override
    public int unsubTicker(String ticker, String sessionId) {
        store.get(ticker).remove(sessionId);
        users.remove(sessionId);
        logging(ticker, sessionId);
        return store.get(ticker).size();
    }

    @Override
    public void setKisSession(WebSocketSession session) {
        infos.put("session", session);
    }

    @Override
    public void removeKisSession(WebSocketSession session) {
        infos.remove(session.getId());
    }

    @Override
    public Optional<WebSocketSession> getKisSession() {
        return Optional.of((WebSocketSession) infos.getOrDefault("session", null));
    }

    @Override
    public Optional<String> findTickerBySessionId(String sessionId) {
        return Optional.of(users.getOrDefault(sessionId, null));
    }

    @Override
    public boolean isStompSessionId(String sessionId) {
        return users.containsKey(sessionId);
    }

    @Override
    public Set<String> getTickers() {
        return store.keySet();
    }

    @Override
    public String getApprovalKey() {
        return (String) infos.get("approvalKey");
    }

    private void logging(String ticker, String user) {
        log.info("session id in {} = (size={}) {}", ticker, store.get(ticker).size(), store.get(ticker));
        log.info("session id in {} = {}", ticker, store.get(ticker));
    }
}
