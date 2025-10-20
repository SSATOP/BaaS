package com.baas.securities.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StockOrderDTO {

    private String accountId;
    private String ticker;
    private String orderType;
    private Long quantity;
    private Double price;
    private String currentType;
}
