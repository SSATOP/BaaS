package com.baas.securities.exception.ex;

public class TooManyRequestsException extends HttpBaseException {
    public TooManyRequestsException(String message, String code) {
        super(message, code);
    }
}
