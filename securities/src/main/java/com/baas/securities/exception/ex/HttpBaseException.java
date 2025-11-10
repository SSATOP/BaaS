package com.baas.securities.exception.ex;

import lombok.Getter;

@Getter
public class HttpBaseException extends RuntimeException {
    private final String code;
    public HttpBaseException(String message, String code) {
        super(message);
        this.code = code;
    }
}
