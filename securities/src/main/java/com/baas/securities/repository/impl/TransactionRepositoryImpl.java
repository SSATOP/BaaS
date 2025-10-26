package com.baas.securities.repository.impl;

import com.baas.securities.repository.TransactionRepository;
import com.baas.securities.repository.dao.TransactionDao;
import com.baas.securities.repository.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Qualifier("TransactionRepository")
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    //private final Map<String, Transaction> store = new ConcurrentHashMap<>();
    private final TransactionDao transactionDao; // 5. DAO 주입받기
    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactionDao.findById(id));

    }

    @Override
    public void save(Transaction transaction) {

        transactionDao.insert(transaction); // DB에 INSERT 실행
    }
}
