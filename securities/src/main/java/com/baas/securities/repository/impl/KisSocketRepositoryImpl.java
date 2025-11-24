package com.baas.securities.repository.impl;

import com.baas.securities.repository.KisSocketRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Qualifier("KisSocketRepository")
public class KisSocketRepositoryImpl implements KisSocketRepository {

    //    @Value("${KIS_WEBSOCKET_APPROVAL_KEY}")
    private String approvalKey="faesfasdfasdf";

    // ---  2. 기존 Map 변수들(users, store, infos) 삭제  ---
    // private final Map<String, String> users = ... (삭제)
    // private final Map<String, CopyOnWriteArrayList<String>> store = ... (삭제)
    // private final Map<String, Object> infos = ... (삭제)

    // ---  3. Redis 템플릿과 KIS 세션 변수 추가  ---

    /**
     * Redis를 사용하기 위한 핵심 도구 (String 특화)
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * KIS 실제 웹소켓 세션. (Redis에 저장 불가!)
     * 이 객체는 이 서버 인스턴스의 메모리에만 존재해야 합니다.
     * 'volatile' 키워드는 멀티 스레드 환경에서 변수 변경 사항을 즉시 반영합니다.
     */
    private volatile WebSocketSession kisSession = null;

    /**
     * Redis 키 접두사 (Key 관리용)
     */
    private static final String KEY_STORE_TICKER_PREFIX = "kis:store:ticker:"; // (종목별 세션 Set)
    private static final String KEY_USERS_SESSION = "kis:users:session";       // (세션-종목 Hash)
    private static final String KEY_INFOS_APPROVAL = "kis:infos:approvalKey";  // (승인 키 String)

    /**
     * 4. 생성자 주입
     * Spring이 StringRedisTemplate Bean을 여기에 주입해 줍니다.
     */
    public KisSocketRepositoryImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    private void init() {
        // infos.put("approvalKey", approvalKey); (삭제)

        // 5. approvalKey를 Redis에 저장
        updateApprovalKey(approvalKey);
    }

    @Override
    public int subTicker(String ticker, String sessionId) {
        // store.computeIfAbsent(ticker, k -> new CopyOnWriteArrayList<>()).add(sessionId); (삭제)
        // users.put(sessionId, ticker); (삭제)

        // 6. Redis로 대체
        // (store -> Set 자료구조) "어떤 Ticker에 어떤 세션들이 있는지"
        stringRedisTemplate.opsForSet().add(KEY_STORE_TICKER_PREFIX + ticker, sessionId);

        // (users -> Hash 자료구조) "어떤 세션이 어떤 Ticker를 보는지"
        stringRedisTemplate.opsForHash().put(KEY_USERS_SESSION, sessionId, ticker);

        logging(ticker, sessionId);
        // 현재 총 인워수 반환
        Long count = stringRedisTemplate.opsForSet().size(KEY_STORE_TICKER_PREFIX + ticker);
        return (count == null) ? 0 : count.intValue();

    }

    @Override
    public boolean haveSubscriber(String ticker) {
        // return store.containsKey(ticker) && !store.get(ticker).isEmpty(); (삭제)

        // 7. Redis로 대체 (Set에 멤버가 있는지 확인)
        Long count = stringRedisTemplate.opsForSet().size(KEY_STORE_TICKER_PREFIX + ticker);
        return (count != null && count > 0);
    }

    @Override
    public int unsubTicker(String ticker, String sessionId) {
        // store.get(ticker).remove(sessionId); (삭제)
        // users.remove(sessionId); (삭제)

        // 8. Redis로 대체
        // (store -> Set에서 제거)
        stringRedisTemplate.opsForSet().remove(KEY_STORE_TICKER_PREFIX + ticker, sessionId);
        // (users -> Hash에서 제거)
        stringRedisTemplate.opsForHash().delete(KEY_USERS_SESSION, sessionId);

        logging(ticker, sessionId);

        Long count = stringRedisTemplate.opsForSet().size(KEY_STORE_TICKER_PREFIX + ticker);
        return (count == null) ? 0 : count.intValue();
    }

    @Override
    public void setKisSession(WebSocketSession session) {
        // infos.put("session", session); (삭제)

        // 9. 로컬 변수에 저장 (Redis 아님!)
        this.kisSession = session;
    }

    @Override
    public void removeKisSession(WebSocketSession session) {
        // infos.remove(session.getId()); (삭제) (-> 로직 변경 필요)

        // 10. 로컬 변수에서 제거 (및 세션 닫기)
        if (this.kisSession != null && this.kisSession.getId().equals(session.getId())) {
            try {
                this.kisSession.close(); // 실제 세션 연결도 닫아줌
            } catch (IOException e) {
                log.warn("KIS 세션 닫기 실패: {}", e.getMessage());
            }
            this.kisSession = null;
        }
    }

    @Override
    public Optional<WebSocketSession> getKisSession() {
        // return Optional.of((WebSocketSession) infos.getOrDefault("session", null)); (삭제)

        // 11. 로컬 변수에서 반환
        return Optional.ofNullable(this.kisSession);
    }

    @Override
    public Optional<String> findTickerBySessionId(String sessionId) {
        // return Optional.of(users.getOrDefault(sessionId, null)); (삭제)

        // 12. Redis Hash에서 조회
        Object ticker = stringRedisTemplate.opsForHash().get(KEY_USERS_SESSION, sessionId);
        return Optional.ofNullable((String) ticker);
    }

    @Override
    public boolean isStompSessionId(String sessionId) {
        // return users.containsKey(sessionId); (삭제)

        // 13. Redis Hash에 키가 있는지 확인
        return stringRedisTemplate.opsForHash().hasKey(KEY_USERS_SESSION, sessionId);
    }

    @Override
    public Set<String> getTickers() {
        // return store.keySet(); (삭제)

        // 14. Redis에서 "kis:store:ticker:"로 시작하는 모든 키를 찾음
        Set<String> keys = stringRedisTemplate.keys(KEY_STORE_TICKER_PREFIX + "*");
        if (keys == null) {
            return Set.of(); // 빈 Set 반환
        }

        // "kis:store:ticker:005930" -> "005930" 으로 변환
        int prefixLength = KEY_STORE_TICKER_PREFIX.length();
        return keys.stream()
                .map(key -> key.substring(prefixLength))
                .collect(Collectors.toSet());
    }

    @Override
    public String getApprovalKey() {
        // return (String) infos.get("approvalKey"); (삭제)

        // 15. Redis에서 조회
        return stringRedisTemplate.opsForValue().get(KEY_INFOS_APPROVAL);
    }

    /*
     * 17. KIS Approval Key 갱신 메소드 구현
     */
    @Override
    public void updateApprovalKey(String newApprovalKey) {
        // 'init()' 메소드에서 사용한 것과 동일한 키에 새 값을 덮어씁니다.
        stringRedisTemplate.opsForValue().set(KEY_INFOS_APPROVAL, newApprovalKey);
        log.info("KIS Approval Key가 Redis에 갱신되었습니다. New Key (일부): {}",
                newApprovalKey.substring(0, Math.min(newApprovalKey.length(), 10)) + "...");
    }

    private void logging(String ticker, String user) {
        // 16. 로깅 로직도 Redis 기반으로 수정
        Long size = stringRedisTemplate.opsForSet().size(KEY_STORE_TICKER_PREFIX + ticker);
        Set<String> members = stringRedisTemplate.opsForSet().members(KEY_STORE_TICKER_PREFIX + ticker);
        log.info("session id in {} = (size={}) {}", ticker, size, members);
    }


    /**
     * [추가 구현] 현재 보관 중인 세션을 강제로 종료하고 null 처리
     */
    @Override
    public void disconnect() {
        WebSocketSession session = this.kisSession;
        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
                log.info("KIS WebSocket 연결 종료 완료: {}", session.getId());
            } catch (IOException e) {
                log.error("KIS WebSocket 종료 중 오류 발생", e);
            } finally {
                this.kisSession = null;
            }
        }
    }

}