package com.baas.securities.dto.account;

import com.baas.securities.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 입출금 거래 요청용
 */
@Getter
@NoArgsConstructor
public class TransactionReqDTO {
    private TransactionType transactionType;
    private BigDecimal amount;
}
