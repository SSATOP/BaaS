package com.baas.securities.service;

import com.baas.securities.dto.transaction.TransactionsReqDTO;
import com.baas.securities.repository.TransactionRepository;
import com.baas.securities.repository.entity.TransactionOrder;
import com.baas.securities.repository.entity.TransactionRelative;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * 입출금, 주식 거래 내역을 구분할 필요가 있음.
     */

    /**
     * 주식 거래 내역 조회 메소드
     */
    public List<TransactionOrder> transactionOrders(TransactionsReqDTO dto) {
        return transactionRepository.findAllTransactionOrder(dto);
    }

    /**
     * 계좌간 거래 내역 조회 메소드
     */
    public List<TransactionRelative> transactionRelatives(TransactionsReqDTO dto) {
        return transactionRepository.findAllTransactionRelative(dto);
    }
}
