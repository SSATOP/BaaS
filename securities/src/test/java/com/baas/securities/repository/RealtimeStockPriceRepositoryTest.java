package com.baas.securities.repository;

import com.baas.securities.repository.entity.RealTimePrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RealtimeStockPriceRepositoryTest {

    @Autowired
    RealtimeStockPriceRepository repository;

    @Test
    @DisplayName("find")
    void find() {
        // given
        RealTimePrice realTimePrice = repository.findTopByTickerOrderByTradeTimeDesc("005930").get();
        // when
        System.out.println(realTimePrice);
        // then
    }
}