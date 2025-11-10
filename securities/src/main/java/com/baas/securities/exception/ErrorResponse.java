package com.baas.securities.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// 공통 에러 응답 객체
public record ErrorResponse(
        Integer status,
        String code,       // 에러 코드 (ex: ACCOUNT-4001)
        String message,    // 에러 메시지 (ex: "잔액이 부족합니다.")
        LocalDateTime timestamp // 발생 시간
) {
    // 정적 팩토리 메서드 (생성 시점 자동 처리)
    public static ErrorResponse of(Integer status, String code, String message) {
        return new ErrorResponse(status, code, message, LocalDateTime.now());
    }
}