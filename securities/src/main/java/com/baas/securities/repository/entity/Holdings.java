package com.baas.securities.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Holdings {
    private String id;
    private String symbol;
    private String userId;
    private Long quantity;
    private Double avgPrice;
    private Double profitLoss;
    private LocalDateTime lastUpdated;


    @Builder
    public Holdings(String symbol, String userId, Long quantity, Double avgPrice, Double profitLoss, LocalDateTime lastUpdated) {
        this.symbol = symbol;
        this.userId = userId;
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
}
