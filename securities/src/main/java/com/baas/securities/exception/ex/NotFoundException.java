package com.baas.securities.exception.ex;

import lombok.Getter;

public class NotFoundException extends HttpBaseException {
    public NotFoundException(String message, String code) {
        super(message, code);
    }
}
