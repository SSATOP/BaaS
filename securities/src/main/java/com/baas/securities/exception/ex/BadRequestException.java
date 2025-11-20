package com.baas.securities.exception.ex;

import com.baas.securities.exception.ErrorCode;
import lombok.Getter;

// 400 BAD_REQUEST
@Getter
public class BadRequestException extends HttpBaseException {
    public BadRequestException(String message, String code) {
        super(message, code);
    }
    //ErrorCode enum을 기반으로 예외를 생성합니다.
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
