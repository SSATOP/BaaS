package com.baas.securities.service;

import com.baas.securities.dto.stock.RealtimeStockDTO;
import com.baas.securities.enums.Period;
import com.baas.securities.repository.PeriodStockPriceRepository;
import com.baas.securities.repository.entity.PeriodStockPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PeriodHttpServiceTest {

    @Autowired
    PeriodHttpService service;
    @Autowired
    PeriodStockPriceRepository repository;

    @Test
    @DisplayName("day")
    void day() {
        // given
        String samsung = "005930";
        service.saveDay(samsung);
        // when
        PeriodStockPrice periodStockPrice = repository.findFirstByTickerAndPeriodOrderByDateTimeDesc(samsung, Period.DAY).get();
        // then
        System.out.println(periodStockPrice);
        periodStockPrice.getDatas().stream().forEach(System.out::println);
    }

    @Test
    @DisplayName("week")
    void week() {
        // given
        String samsung = "005930";
        service.saveWeek(samsung);
        // when
        PeriodStockPrice periodStockPrice = repository.findFirstByTickerAndPeriodOrderByDateTimeDesc(samsung, Period.WEEK).get();
        // then
        System.out.println(periodStockPrice);
        periodStockPrice.getDatas().stream().forEach(System.out::println);
    }

    @Test
    @DisplayName("month")
    void month() {
        // given
        String samsung = "005930";

        service.saveMonth(samsung);
        // when
        PeriodStockPrice periodStockPrice = repository.findFirstByTickerAndPeriodOrderByDateTimeDesc(samsung, Period.MONTH).get();
        // then
        System.out.println(periodStockPrice);
        periodStockPrice.getDatas().stream().forEach(System.out::println);
    }

    @Test
    void year() {
        String samsung = "005930";

        service.saveYear(samsung);

        PeriodStockPrice periodStockPrice = repository.findFirstByTickerAndPeriodOrderByDateTimeDesc(samsung, Period.YEAR).get();
        // then
        System.out.println(periodStockPrice);
        periodStockPrice.getDatas().stream().forEach(System.out::println);
    }
}