package com.baas.bank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountListResponse {
    private String status;           // "SUCCESS" or "NO_ACCOUNT"
    private List<AccountResponse> accounts;
}

