package com.baas.securities.dto;


import com.baas.securities.enums.OrderStatus;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderResDTO {
    private String orderId;
    private OrderStatus status;
}
