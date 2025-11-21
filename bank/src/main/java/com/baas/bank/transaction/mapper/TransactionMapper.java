package com.baas.bank.transaction.mapper;

import com.baas.bank.transaction.dto.TransactionDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
    void insertTransaction(TransactionDto transaction);
    
    TransactionDto findById(Long id);
}

