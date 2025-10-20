package com.baas.securities.repository.entity;

import com.baas.securities.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter

public class Order {

    private String id;                // 주문 아이디
    private String userId;            // 사용자 아이디
    private String accountId;         // 계좌 아이디
    private String symbol;            // 종목 코드
    private String orderType;         // 주문 종류
    private OrderStatus orderStatus;       // 주문 상태
    private Long quantity;            // 주문량
    private Double price;               // 한 주당 가격
    private Double totalAmount;         // 총 주문 금액
    private LocalDateTime createdAt;  // 주문 시점
    private LocalDateTime completedAt;// 주문 완료 시점
    private String failReason;        // 주문 실패 사유

    @Builder

    public Order(String userId, String accountId, String symbol, String orderType, OrderStatus orderStatus, Long quantity, Double price, Double totalAmount, LocalDateTime createdAt, LocalDateTime completedAt, String failReason) {
        this.userId = userId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.failReason = failReason;

        id = UUID.randomUUID().toString();
    }

    public void updateOrderStatus(OrderStatus orderStatus, String failReason){
        this.orderStatus = orderStatus;
        this.failReason =failReason;

    }
}
