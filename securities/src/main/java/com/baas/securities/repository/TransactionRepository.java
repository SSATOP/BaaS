package com.baas.securities.repository;

import com.baas.securities.dto.transaction.TransactionsReqDTO;
import com.baas.securities.repository.entity.Transaction;
import com.baas.securities.repository.entity.TransactionOrder;
import com.baas.securities.repository.entity.TransactionRelative;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Optional<Transaction> findById(String id);

    void save(Transaction transaction);

    List<TransactionOrder> findAllTransactionOrder(TransactionsReqDTO dto);

    List<TransactionRelative> findAllTransactionRelative(TransactionsReqDTO dto);
}
