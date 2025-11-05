package com.baas.securities.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
/**
 * 입출금 거래 응답용
 */
@Getter
@AllArgsConstructor
public class TransactionResDTO {
    private String transactionId;
    private BigDecimal updatedBalance;
}
