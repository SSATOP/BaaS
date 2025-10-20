package com.baas.securities.repository;

import com.baas.securities.repository.entity.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Optional<Transaction> findById(String id);

    void save(Transaction transaction);
}
