package com.baas.securities.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 리스트 조회시 사용될 주식 정보 DTO
 */

public class StockInfoDTO {
    private String ticker;
    private String stockName;
    private double currentPrice;
    private double change;
    private long dataRank;
    private double changeRate;
    private String changeSign;
}
