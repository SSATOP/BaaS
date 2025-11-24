package com.baas.securities.dto.transaction;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class TransactionsReqDTO {
    private String accountId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isOrder;
}
