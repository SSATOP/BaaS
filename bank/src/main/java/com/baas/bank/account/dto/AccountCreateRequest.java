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
    // [테스트용] 로그인 없이 userId만으로 테스트하는 경우 사용
    private Long userId;         // 사용자 ID
    private String bankCode;     // 은행 코드
    private String password;     // 계좌 비밀번호
    private String alias;        // 별칭 (선택사항)
}

