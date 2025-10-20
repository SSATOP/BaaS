package com.baas.bank.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthException extends AuthenticationException {
    private final String errorCode;

    public JwtAuthException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public JwtAuthException(String errorCode, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // JWT 관련 에러 코드 상수들
    public static final String EMPTY_EMAIL_OR_PASSWORD = "EMPTY_EMAIL_OR_PASSWORD";
    public static final String INVALID_EMAIL_OR_PASSWORD = "INVALID_EMAIL_OR_PASSWORD";
    public static final String UNSUPPORTED_TYPE = "UNSUPPORTED_TYPE";
    public static final String EXPIRED_TOKEN = "EXPIRED_TOKEN";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String INVALID_USER = "INVALID_USER";
    public static final String TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND";
    public static final String DB_ERROR = "DB_ERROR";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
}
