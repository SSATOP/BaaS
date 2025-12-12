package com.baas.securities.scheduler;

import com.baas.securities.enums.Ticker;
import com.baas.securities.service.PeriodHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceScheduler {

    private final PeriodHttpService service;

    // 예시 1: 매분 0초마다 실행 (CRON 표현식 사용)
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void collectStockData() {
        log.info("주식 데이터 수집 시작 : {}", LocalDateTime.now());

        for (Ticker ticker : Ticker.values()) {
            save(ticker.getName());
        }

        log.info("주식 데이터 저장 완료");
    }

    private void save(String ticker) {
        service.saveDay(ticker);
        service.saveWeek(ticker);
        service.saveMonth(ticker);
        service.saveYear(ticker);
    }

    // 예시 2: 이전 작업 종료 후 1초(1000ms) 대기 후 실행
    @Scheduled(fixedDelay = 1000)
    public void monitoringTask() {
        // 실시간 모니터링 로직 등
    }
}
