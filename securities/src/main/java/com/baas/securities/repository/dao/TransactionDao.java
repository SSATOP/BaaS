package com.baas.securities.repository.dao;

import com.baas.securities.repository.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionDao {
    void insert(Transaction transaction);
    Transaction findById(String id);
}
