package com.baas.securities.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class Symbol {
    private String symbol;
    private String name;
    private String market;
    private String isin;
    private String sector;
    private String industry;
    private LocalDateTime listed_at;
}
