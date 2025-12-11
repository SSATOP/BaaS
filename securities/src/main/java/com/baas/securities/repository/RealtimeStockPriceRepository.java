package com.baas.securities.repository;

import com.baas.securities.repository.entity.RealTimePrice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RealtimeStockPriceRepository extends MongoRepository<RealTimePrice, String> {
    Optional<RealTimePrice> findTopByTickerOrderByTradeTimeDesc(String ticker);
}
