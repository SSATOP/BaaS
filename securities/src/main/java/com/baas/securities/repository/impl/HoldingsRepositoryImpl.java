package com.baas.securities.repository.impl;

import com.baas.securities.repository.HoldingsRepository;
import com.baas.securities.repository.entity.Holdings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Qualifier("HoldingsRepository")
public class HoldingsRepositoryImpl implements HoldingsRepository {
    private final Map<String, Holdings> store = new ConcurrentHashMap<>();

    @Override
    public Optional<Holdings> findById(String id) {
        Holdings holdings = store.get(id);
        return Optional.of(holdings);
    }

    @Override
    public void save(Holdings holdings) {
        store.put(holdings.getId(), holdings);
    }

    @Override
    public Optional<Holdings> findByUserIdAndTicker(String userId, String ticker) {
        return store.values().stream()
                .filter(holdings -> holdings.getUserId().equals(userId) && holdings.getSymbol().equals(ticker))
                .findFirst();
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
