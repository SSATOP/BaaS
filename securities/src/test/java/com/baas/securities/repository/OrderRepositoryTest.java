package com.baas.securities.repository;

import com.baas.securities.enums.OrderStatus;
import com.baas.securities.enums.TickerDummy;
import com.baas.securities.enums.TransactionType;
import com.baas.securities.repository.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("생성 테스트")
    void saveTest() {
        // given
        Order order = Order.builder()
                .price(new BigDecimal(10000))
                .accountId("0a5718b5-e040-48f1-977a-ce33c36314cd")
                .userId("3cd332f7-df2c-4e49-a315-2cd3ee71c6d3")
                .orderType(TransactionType.BUY.name())
                .quantity(10L)
                .orderStatus(OrderStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .symbol(TickerDummy.SK.getName())
                .build();
        // when
        orderRepository.save(order);
        // then
    }

}