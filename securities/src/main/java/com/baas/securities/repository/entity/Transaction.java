package com.baas.securities.repository.entity;

import com.baas.securities.enums.TransactionStatus;
import com.baas.securities.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter

public class Transaction {
    private String id;                // 거래 아이디
    private String orderId;           // 주문 아이디
    private String accountId;         // 계좌 아이디
    private TransactionType transactionType;   // 거래 종류
    private BigDecimal amount;              // 금액
    private TransactionStatus status;            // 상태
    private String failReason;        // 실패 이유
    private LocalDateTime createdAt;  // 생성일
    private LocalDateTime completedAt;// 완료일
    private String relativeAccountNumber;       // 송금 목표 계좌 번호
    private String relativeAccountUsername;     // 송금 목표 계좌 예금주

    @Builder
    public Transaction(String orderId, String accountId, TransactionType transactionType, BigDecimal amount, TransactionStatus status, String failReason, LocalDateTime createdAt, LocalDateTime completedAt, String relativeAccountNumber, String relativeAccountUsername) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.failReason = failReason;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.relativeAccountNumber = relativeAccountNumber;
        this.relativeAccountUsername = relativeAccountUsername;
        id = UUID.randomUUID().toString();
    }
}
