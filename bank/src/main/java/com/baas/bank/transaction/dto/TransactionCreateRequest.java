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
    private Long accId;              // 계좌 ID (송금 계좌)
    private Long amount;             // 거래 금액
    private String password;         // 계좌 비밀번호 (출금/이체 시 필수)
    private String memo;             // 메모 (선택사항)
    private Long targetAccId;        // 수신 계좌 ID (이체 시 필수)
    private String targetAccBankcode; // 수신 계좌 은행 코드 (이체 시 필수)
}

