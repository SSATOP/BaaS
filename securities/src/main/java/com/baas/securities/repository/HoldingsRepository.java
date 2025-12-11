package com.baas.securities.repository;


import com.baas.securities.repository.entity.Account;
import com.baas.securities.repository.entity.Holdings;

import java.util.List;
import java.util.Optional;

public interface HoldingsRepository {
    Optional<Holdings> findById(String id);

    void save(Holdings holdings);

    Optional<Holdings> findByAccountIdAndTicker(String accountId, String ticker);

    void deleteById(String id);

    List<Holdings> findAllByAccountId(String accountId);
}
