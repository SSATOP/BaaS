package com.baas.bank.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor getMailAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);    // 기본적으로 실행을 대기하고 있는 스레드의 수
        executor.setMaxPoolSize(5);     // 동시에 동작할 수 있는 최대 스레드의 수
        executor.setQueueCapacity(10);  // Core Pool Size를 초과하는 요청이 들어왔을 때,
                                        // 해당 요청을 저장할 수 있는 대기열의 크기
        executor.setThreadNamePrefix("MailExecutor-");  // 로그를 분석하거나 디버깅할 때 식별할 스레드의 이름 접두사를 설정
        executor.initialize();          // 스레드 풀 초기화
        return executor;
    }

}
