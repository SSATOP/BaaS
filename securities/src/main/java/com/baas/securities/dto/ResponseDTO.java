package com.baas.securities.dto;
import lombok.*;
import org.springframework.http.HttpStatus;


// 주문 증권계좌 거래


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    HttpStatus status;
    String message;
    Object data;
}
