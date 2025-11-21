package com.baas.bank.account.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountDto {
    private Long id;                    // id
    private Long userId;                 // user_id
    private String accountNumber;        // account_number
    private String bankCode;             // bank_code
    private String alias;                // alias
    private Long balance;                // balance
    private String password;             // password
    private LocalDateTime lastUpdated;   // last_updated
    private LocalDateTime issuedAt;      // issued_at
    private LocalDateTime expiredAt;     // expired_at
}

