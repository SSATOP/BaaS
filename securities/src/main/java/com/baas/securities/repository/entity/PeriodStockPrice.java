package com.baas.securities.repository.entity;

import com.baas.securities.dto.stock.RealtimeStockDTO;
import com.baas.securities.enums.Period;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "period_stock_price")
@CompoundIndexes({
        @CompoundIndex(name = "ticker_time_idx", def = "{'ticker': 1, 'dateTime': -1}")
})
@ToString
public class PeriodStockPrice {
    @Id
    private String id;
    private String ticker;
    private LocalDateTime dateTime;
    private Period period;
    private List<RealtimeStockDTO> datas;
}
