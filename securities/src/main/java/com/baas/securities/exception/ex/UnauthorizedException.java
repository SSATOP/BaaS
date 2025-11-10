package com.baas.securities.exception.ex;

import com.baas.securities.exception.ErrorCode;
import lombok.Getter;

// 401 UNAUTHORIZED
public class UnauthorizedException extends HttpBaseException {
    public UnauthorizedException(String message, String code) {
        super(message, code);
    }

    //ErrorCode enum을 기반으로 예외를 생성합니다.
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
