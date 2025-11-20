package com.baas.bank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountCreateResponse {
    private Long id;
    private String accountNumber;
    private String bankCode;
    private String alias;
    private Long balance;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
}

