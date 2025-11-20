package com.baas.securities.exception.ex;


import com.baas.securities.exception.ErrorCode;

// 409 CONFLICT
public class ConflictException extends HttpBaseException {
    public ConflictException(String message, String code) {
        super(message,code);
    }
    //ErrorCode enum을 기반으로 예외를 생성합니다.
    public ConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
