package com.baas.securities.dto.stock;

import com.baas.securities.enums.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeriodStockPriceDTO {
    private String ticker;
    private Period period;
    private List<RealtimeStockDTO> datas;
}
