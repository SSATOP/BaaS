package com.baas.securities.exception.ex;


import com.baas.securities.exception.ErrorCode;

// 500 INTERNAL_SERVER_ERROR
public class InternalServerErrorException extends HttpBaseException {
    public InternalServerErrorException(String message, String code) {
        super(message, code);
    }
    //ErrorCode enum을 기반으로 예외를 생성합니다.
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
