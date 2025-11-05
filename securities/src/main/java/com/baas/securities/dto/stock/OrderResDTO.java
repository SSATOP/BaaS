package com.baas.securities.dto.stock;


import com.baas.securities.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderResDTO {
    private String orderId;
    private OrderStatus status;
}
