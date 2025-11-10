package com.baas.securities.exception.ex;

import lombok.Getter;

public class UnauthorizedException extends HttpBaseException {
    public UnauthorizedException(String message, String code) {
        super(message, code);
    }
}
