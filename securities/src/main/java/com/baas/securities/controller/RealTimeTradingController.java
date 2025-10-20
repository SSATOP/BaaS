package com.baas.securities.controller;

import com.baas.securities.dto.OrderResDTO;
import com.baas.securities.dto.ResponseDTO;
import com.baas.securities.dto.StockOrderDTO;
import com.baas.securities.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RealTimeTradingController {
    private final OrderService orderService;

    @MessageMapping("/stock/order")
    @SendTo("/sub/stock/order")
    public ResponseDTO order(@RequestBody StockOrderDTO dto) {
        // Todo : 요청한 사용자와 계좌가 일치하는지 절차 필요
        OrderResDTO orderResDTO = null;
        switch (dto.getOrderType()) {
            case "BUY" -> orderResDTO = orderService.buyOrder(dto);
            case "SELL" -> orderResDTO = orderService.sellOrder(dto);

        }
        return generateResponseDto(HttpStatus.CREATED, "접수 완료", orderResDTO);
    }

    public ResponseDTO generateResponseDto(HttpStatus status, String message, OrderResDTO orderResDTO){
        return ResponseDTO.builder()
                .status(status)
                .message(message)
                .data(orderResDTO)
                .build();
    }

}
