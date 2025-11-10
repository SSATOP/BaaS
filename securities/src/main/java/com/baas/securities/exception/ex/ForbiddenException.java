package com.baas.securities.exception.ex;

public class ForbiddenException extends HttpBaseException {
    public ForbiddenException(String message, String code) {
        super(message, code);
    }
}
