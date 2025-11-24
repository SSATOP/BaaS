package com.baas.securities.scheduler;

import com.baas.securities.repository.KisSocketRepository;
import com.baas.securities.service.KisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class KisScheduler {

    // KIS API를 호출하여 '새 키'를 받아오는 로직이 담긴 서비스
    private final KisService kisService;

    // 받아온 '새 키'를 Redis에 저장하는 레포지토리
    private final KisSocketRepository kisSocketRepository;


    // TODO : api 접근키도 같이하기.
    @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Seoul")
    public void refreshKisApprovalKey() {
        log.info("KIS 승인 키 갱신 스케줄러 시작 (매일 23:55)");

        try {
            // 1. KisService를 통해 KIS API를 호출해서 '새 승인 키'를 발급받습니다.
            //    (이 메소드는 KisService에 새로 만들어야 합니다)
            String newApprovalKey = kisService.fetchNewApprovalKeyFromKisApi();

            if (newApprovalKey != null && !newApprovalKey.isEmpty()) {

                // 2. KisSocketRepository를 통해 '새 승인 키'를 Redis에 덮어씁니다.
                kisSocketRepository.updateApprovalKey(newApprovalKey);
                
                // 웹 소켓 재접속 및 구독 복구 호출
                kisService.refreshWebSocketConnection();
                
                log.info("KIS 승인 키 갱신 성공");

            } else {
                log.warn("KIS 승인 키 갱신 실패: KIS API로부터 유효한 키를 받지 못했습니다.");
            }

        } catch (Exception e) {
            log.error("KIS 승인 키 갱신 중 심각한 오류 발생", e);
        }
    }
}