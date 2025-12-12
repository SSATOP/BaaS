package com.baas.securities.repository.entity;

import com.baas.securities.enums.TransactionStatus;
import com.baas.securities.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionOrder {
    private String id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime completedAt;
    private String failReason;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
}
