package com.baas.securities.exception.ex;

public class InternalServerErrorException extends HttpBaseException {
    public InternalServerErrorException(String message, String code) {
        super(message, code);
    }
}
