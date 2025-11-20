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
    // [테스트용] 로그인 없이 userId만으로 테스트하는 경우 사용
    private Long userId;         // 사용자 ID
    private Long accId;          // 계좌 ID
    private Long amount;         // 입금 금액
    private String memo;         // 메모 (선택사항)
}

