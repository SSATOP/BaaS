package com.baas.securities.dto.stock;

import com.baas.securities.repository.entity.RealTimePrice;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeStockDTO {
    private String ticker;                 // 종목명
    private String tradeTime;              // 체결시간
    private Double price ;                   // 현재가격
    private BigInteger change;             // 전일 대비
    private BigDecimal changeRate;         // 전일 대비율
    private BigInteger tradeVolume;        // 거래량
    private BigInteger accTradeVolume;     // 누적 거래량
    private BigInteger accTradeValue;      // 누적 거래대금
    private Double openPrice;                // 시가
    private Double highPrice;                // 고가
    private Double lowPrice;                 // 저가

    public RealTimePrice toEntity() {
        return RealTimePrice.builder()
                .ticker(ticker)
                .tradeTime(formatTradeTime(tradeTime))
                .price(price)
                .change(change)
                .changeRate(changeRate)
                .tradeVolume(tradeVolume)
                .accTradeVolume(accTradeVolume)
                .accTradeValue(accTradeValue)
                .openPrice(openPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .build();
    }

    private LocalDateTime formatTradeTime(String timeString) {
        LocalTime date = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));

        LocalDateTime localDateTime = date.atDate(LocalDate.now());
        return localDateTime;
    }
}
