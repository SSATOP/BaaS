package com.baas.bank.transaction.mapper;

import com.baas.bank.transaction.dto.TransactionDto;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransactionMapper {
    void insertTransaction(TransactionDto transaction);
    
    TransactionDto findById(Long id);
    
    List<TransactionDto> findByAccId(@Param("accId") Long accId);
}

