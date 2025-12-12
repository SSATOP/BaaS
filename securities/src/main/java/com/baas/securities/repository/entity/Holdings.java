package com.baas.securities.repository.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Holdings {
    private String id;
    private String symbol;
    private String accountId;
    private Long quantity;
    private Double avgPrice;
    private Double profitLoss;
    private LocalDateTime lastUpdated;


    @Builder
    public Holdings(String symbol, String accountId, Long quantity, Double avgPrice, Double profitLoss, LocalDateTime lastUpdated) {
        this.symbol = symbol;
        this.accountId = accountId;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.profitLoss = profitLoss;
        this.lastUpdated = lastUpdated;
        id = UUID.randomUUID().toString();
    }
    // todo : 종목별 수량 및 현재가 손실 관리 필요
    public void updateQuantity(Long quantity){
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public void updateAvgPrice(Long quantity, BigDecimal curPrice) {
        double have = avgPrice * this.quantity;

        this.avgPrice = curPrice
                .multiply(new BigDecimal(quantity))
                .add(new BigDecimal(have))
                .divide(new BigDecimal(this.quantity + quantity))
                .doubleValue();
    }
}
