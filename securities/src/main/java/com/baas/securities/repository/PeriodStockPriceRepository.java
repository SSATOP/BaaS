package com.baas.securities.repository;

import com.baas.securities.enums.Period;
import com.baas.securities.repository.entity.PeriodStockPrice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PeriodStockPriceRepository extends MongoRepository<PeriodStockPrice, String> {
    Optional<PeriodStockPrice> findFirstByTickerAndPeriodOrderByDateTimeDesc(String ticker, Period period);
}
