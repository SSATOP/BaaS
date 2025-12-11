package com.baas.securities.dto.stock;

import com.baas.securities.enums.Period;
import lombok.Data;

@Data
public class PeriodStockRequestDTO {
    private String ticker;
    private Period period;
}
