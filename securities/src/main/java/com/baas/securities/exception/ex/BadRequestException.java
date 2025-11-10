package com.baas.securities.exception.ex;

import lombok.Getter;

public class BadRequestException extends HttpBaseException {
    public BadRequestException(String message, String code) {
        super(message, code);
    }
}
