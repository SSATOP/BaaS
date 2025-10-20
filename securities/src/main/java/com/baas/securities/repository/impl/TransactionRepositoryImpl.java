package com.baas.securities.repository.impl;

import com.baas.securities.repository.TransactionRepository;
import com.baas.securities.repository.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<String, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public Optional<Transaction> findById(String id) {
        Transaction transaction = store.get(id);
        return Optional.of(transaction);
    }

    @Override
    public void save(Transaction transaction) {
        store.put(transaction.getId(),transaction);
    }
}
