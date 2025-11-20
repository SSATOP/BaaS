package com.baas.bank.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TransactionCreateResponse {
    private Long id;                    // 거래 ID
    private Long accId;                 // 계좌 ID
    private Long type;                  // 거래 유형 (1: DEPOSIT)
    private Long amount;                 // 거래 금액
    private Long balanceAfter;          // 거래 후 잔액
    private LocalDateTime txDatetime;   // 거래 일시
    private String memo;                // 메모
}

