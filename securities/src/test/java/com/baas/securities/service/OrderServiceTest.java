package com.baas.securities.service;

import com.baas.securities.dto.AccIdUserIdInfoDTO;
import com.baas.securities.dto.stock.StockOrderDTO;
import com.baas.securities.enums.OrderStatus;
import com.baas.securities.enums.TickerDummy;
import com.baas.securities.enums.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    /**
     * {
     *   "accountId": "110-234...",
     *   "ticker": "005930",
     *   "orderType": "SELL",
     *   "quantity": 10,
     *   "price": 80000,
     *   "currentType" : "원화"
     * }
     */
    @Test
    @DisplayName("매수 주문")
    void buyOrder() {
        // given
        // "user2@gmail.com", "867-859-577865"
        StockOrderDTO dto = new StockOrderDTO();
        dto.setOrderType(TransactionType.BUY.name());
        dto.setTicker(TickerDummy.SS.getName());
        dto.setPrice(1000d);
        dto.setQuantity(2L);
        dto.setCurrentType("원화");
        dto.setAccountNumber("867-859-577865");

        AccIdUserIdInfoDTO infoDto = new AccIdUserIdInfoDTO("3cd332f7-df2c-4e49-a315-2cd3ee71c6d3", "0a5718b5-e040-48f1-977a-ce33c36314cd");

        // when
        orderService.buyOrder(dto, infoDto);
        // then

    }

    @Test
    @DisplayName("매도 주문")
    void sellOrder() {
        // given
        StockOrderDTO dto = new StockOrderDTO();
        dto.setOrderType(TransactionType.SELL.name());
        dto.setTicker(TickerDummy.SS.getName());
        dto.setPrice(900d);
        dto.setQuantity(2L);
        dto.setCurrentType("원화");
        dto.setAccountNumber("867-859-577865");
        // when
        AccIdUserIdInfoDTO infoDto = new AccIdUserIdInfoDTO("3cd332f7-df2c-4e49-a315-2cd3ee71c6d3", "0a5718b5-e040-48f1-977a-ce33c36314cd");

        Assertions.assertEquals("005930", dto.getTicker());
        orderService.sellOrder(dto, infoDto);
        // then
    }

}