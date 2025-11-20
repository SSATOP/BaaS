package com.baas.securities.exception.ex;

import com.baas.securities.exception.ErrorCode;
import lombok.Getter;


// 404 NOT_FOUND
public class NotFoundException extends HttpBaseException {
    public NotFoundException(String message, String code) {
        super(message, code);
    }
    //ErrorCode enum을 기반으로 예외를 생성합니다.
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
