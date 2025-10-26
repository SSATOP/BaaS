package com.baas.securities.dto.stock;

import lombok.Getter;
import lombok.Setter;

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
