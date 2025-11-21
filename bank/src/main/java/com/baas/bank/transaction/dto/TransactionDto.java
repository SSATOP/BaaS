package com.baas.bank.transaction.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionDto {
    private Long id;                    // id
    private Long accId;                 // acc_id
    private Long targetAccId;           // target_acc_id
    private String targetAccBankcode;   // target_acc_bankcode
    private Long type;                  // type (1: DEPOSIT, 2: WITHDRAW, 3: TRANSFER 등)
    private Long amount;                 // amount
    private Long balanceAfter;          // balance_after
    private LocalDateTime txDatetime;   // tx_datetime
    private String memo;                // memo
    private LocalDateTime createdAt;    // created_at
}

