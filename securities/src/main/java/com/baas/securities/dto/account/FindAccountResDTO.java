package com.baas.securities.dto.account;

import com.baas.securities.repository.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FindAccountResDTO {
    private String accountNumber;
    private BigDecimal balance;        // 계좌 잔고
    private String accountType;        // 계좌 타입

    public static FindAccountResDTO generate(Account account) {
        return FindAccountResDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .build();
    }
}
