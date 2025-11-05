package com.baas.securities.repository.dao;

import com.baas.securities.repository.TransactionRepository;
import com.baas.securities.repository.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface TransactionDao extends TransactionRepository {
    void insert(Transaction transaction);
    Optional<Transaction> findById(String id);
}
