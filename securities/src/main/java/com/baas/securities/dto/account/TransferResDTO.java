package com.baas.securities.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TransferResDTO {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal transferredAmount;
    private BigDecimal updatedFromBalance;
}
