package com.baas.securities.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "realtime_stock_price")
@CompoundIndexes({
        @CompoundIndex(name = "ticker_time_idx", def = "{'ticker': 1, 'tradeTime': -1}")
})
public class RealTimePrice {
    @Id
    private String id;

    private String ticker;                 // 종목명
    private LocalDateTime tradeTime;              // 체결시간
    private Double price;                   // 현재가격
    private BigInteger change;             // 전일 대비
    private BigDecimal changeRate;         // 전일 대비율
    private BigInteger tradeVolume;        // 거래량
    private BigInteger accTradeVolume;     // 누적 거래량
    private BigInteger accTradeValue;      // 누적 거래대금
    private Double openPrice;                // 시가
    private Double highPrice;                // 고가
    private Double lowPrice;                 // 저가
}
