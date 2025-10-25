package com.baas.securities.dto.account;

import com.baas.securities.repository.entity.Account;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@Builder
/**
 * api 명세서에 account name 삭제 필요
 */
public class CreateAccountResDTO {
    private String accountId;
    private String accountNumber;
    private BigDecimal balance;

    public static CreateAccountResDTO generate(Account account) {
        return CreateAccountResDTO.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }
}
