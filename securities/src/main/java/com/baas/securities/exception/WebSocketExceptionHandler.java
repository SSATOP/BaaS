package com.baas.securities.exception;

import com.baas.securities.exception.ex.BadRequestException;
import com.baas.securities.exception.ex.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(BadRequestException.class)
    @SendToUser("/sub/stock/order")
    public ErrorResponse handleBadRequest(BadRequestException e) {
        log.info("bad request msg = {}", e.getMessage());
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getCode(), e.getMessage());
    }

    @MessageExceptionHandler(NotFoundException.class)
    @SendToUser("/sub/stock/order")
    public ErrorResponse handleNotFound(NotFoundException e) {
        log.info("not found exception = {}", e.getMessage());

        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), e.getCode(), e.getMessage());
    }
}
