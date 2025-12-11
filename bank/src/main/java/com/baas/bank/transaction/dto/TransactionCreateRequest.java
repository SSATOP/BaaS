package com.baas.bank.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionCreateRequest {
    private Long accId;          // 계좌 ID
    private Long amount;         // 거래 금액
    private String password;     // 계좌 비밀번호 (출금 시 필수)
    private String memo;         // 메모 (선택사항)
}

