package com.baas.securities.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 enum으로 전체 관리
 * - 1차: HTTP Status Code
 * - 2차: 기능별 세부 카테고리 (주석)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST (잘못된 요청)
    // --- 회원가입 / 사용자 정보 ---
    INVALID_SIGNUP_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "이메일 또는 비밀번호 형식이 올바르지 않습니다."),
    INVALID_USER_UPDATE_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력 데이터가 올바르지 않습니다."),
    NOT_MATCH_USER_EMAIL(HttpStatus.BAD_REQUEST, "NOT_MATCH_USER_EMAIL", "사용자의 이메일과 제공된 이메일의 정보가 일치하지 않습니다."),

    // --- 계좌 / 송금 ---
    INVALID_ACCOUNT_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "계좌 이름 또는 초기 입금액이 올바르지 않습니다."),
    SELF_TRANSFER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "SELF_TRANSFER", "자기 자신에게 송금할 수 없습니다."),
    INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, "INVALID_TRANSACTION", "알 수 없는 트랜잭션 타입입니다."),
    INSUFFICIENT_BALANCE_TRANSACTION(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE", "출금 가능한 잔액이 부족합니다."),

    // --- 주식 / 주문 ---
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "INVALID_ORDER", "주문 수량 또는 가격이 올바르지 않습니다."),
    INSUFFICIENT_BALANCE_ORDER(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE", "계좌 잔액이 부족합니다."),
    INSUFFICIENT_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK_QUANTITY", "보유 수량이 부족합니다. (매도 주문 수량 초과)"),

    // --- 이메일 인증 ---
    INVALID_OR_EXPIRED_CODE(HttpStatus.BAD_REQUEST, "INVALID_OR_EXPIRED_CODE", "인증번호가 올바르지 않거나 만료되었습니다."),
    EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_VERIFIED", "이미 인증된 코드입니다."),

    // --- 기타 ---
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY", "잘못된 카테고리입니다. (예: TOP_GAINERS, VOLUME 등)"),


    // 401 UNAUTHORIZED (인증 실패)
    // --- 공통 인증 ---
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증 토큰이 유효하지 않거나 만료되었습니다."),
    UNAUTHORIZED_LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),

    // --- 계좌 관련 인증 ---
    UNAUTHORIZED_ACCOUNTS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증 토큰이 필요합니다."),
    INVALID_ACCOUNT_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_ACCOUNT_PW", "계좌 비밀번호가 올바르지 않습니다."),


    // 403 FORBIDDEN (접근 권한 없음)
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다."),
    ACCOUNT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FORBIDDEN_ACCOUNT", "해당 계좌에 대한 접근 권한이 없습니다."),
    UNAUTHORIZED_WEBSOCKET(HttpStatus.FORBIDDEN, "UNAUTHORIZED_CONNECTION", "유효한 인증 정보가 없어 WebSocket 연결이 거부되었습니다."),


    // 404 NOT_FOUND (찾을 수 없음)
    // --- 사용자 / 약관 ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자 정보를 찾을 수 없습니다."),
    USER_AGREEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "AGREEMENT_NOT_FOUND", "유저의 사용자 정보 동의를 찾을 수 없습니다."),

    // --- 이메일 인증 ---
    EMAIL_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "TRANSACTION_NOT_FOUND", "해당 인증 요청이 존재하지 않습니다."),
    EMAIL_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND", "해당 이메일의 인증 정보를 찾을 수 없습니다."),

    // --- 계좌 ---
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "해당 계좌를 찾을 수 없습니다."),

    // --- 주식 / 주문 ---
    STOCK_NOT_FOUND_SEARCH(HttpStatus.NOT_FOUND, "STOCK_NOT_FOUND", "검색 결과가 없습니다."),
    STOCK_NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "STOCK_NOT_FOUND", "해당 종목을 찾을 수 없습니다."), // (주문 시 종목 존재 여부)
    STOCK_HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_HOLDING_NOT_FOUND", "판매할 보유 종목을 찾을 수 없습니다."), // (매도 시 보유 여부)
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문 내역이 존재하지 않습니다."),
    TICKER_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKER_NOT_FOUND", "요청한 종목의 시세를 찾을 수 없습니다."),


    // 409 CONFLICT (충돌)
    // --- 사용자 / 약관 ---
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 등록된 이메일입니다."),
    USER_AGREEMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "AGREEMENT_ALREADY_EXISTS", "유저의 사용자 정보 동의가 이미 존재합니다."),

    // --- 계좌 ---
    ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "ACCOUNT_ALREADY_EXISTS", "동일한 이름의 계좌가 이미 존재합니다."),


    // 429 TOO_MANY_REQUESTS (요청 한도 초과)
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "짧은 시간 내 너무 많은 인증 요청이 발생했습니다."),


    // 500 INTERNAL_SERVER_ERROR (서버 내부 오류)
    // --- 공통 서버 오류 ---
    SIGNUP_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "회원가입 처리 중 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED", "이메일 발송 중 오류가 발생했습니다."),

    // --- KIS API / WebSocket ---
    MARKET_DATA_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKET_DATA_ERROR", "시장 지수 정보를 불러오는 중 오류가 발생했습니다."),
    STREAM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "STREAM_ERROR", "실시간 데이터 스트리밍 중 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}