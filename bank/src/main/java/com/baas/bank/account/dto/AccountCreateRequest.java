package com.baas.bank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountCreateRequest {
    private String bankCode;     // 은행 코드
    private String password;     // 계좌 비밀번호
    private String alias;        // 별칭 (선택사항)
}

