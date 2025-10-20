package com.baas.securities.repository;


import com.baas.securities.repository.entity.Holdings;

import java.util.Optional;

public interface HoldingsRepository {
    Optional<Holdings> findById(String id);

    void save(Holdings holdings);

    Optional<Holdings> findByUserIdAndTicker(String userId, String ticker);

    void deleteById(String id);
}
