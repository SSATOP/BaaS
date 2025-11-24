package com.baas.securities.repository.entity;

import com.baas.securities.enums.TransactionStatus;
import com.baas.securities.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRelative {
    private String id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime completedAt;
    private String failReason;
    private String relativeAccountNumber;
    private String relativeAccountUsername;
}
