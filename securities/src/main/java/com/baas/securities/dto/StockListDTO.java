package com.baas.securities.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockListDTO {
    private String category;
    private List<StockInfoDTO> stocks;
}
