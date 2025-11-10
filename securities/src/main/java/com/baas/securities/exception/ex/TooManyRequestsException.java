package com.baas.securities.exception.ex;


import com.baas.securities.exception.ErrorCode;

// 429 TOO_MANY_REQUEST
public class TooManyRequestsException extends HttpBaseException {
    public TooManyRequestsException(String message, String code) {
        super(message, code);
    }
    //ErrorCode enum을 기반으로 예외를 생성합니다.
    public TooManyRequestsException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
