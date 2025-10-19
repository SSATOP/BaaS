package com.baas.securities.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexResDTO {
    private String indexName;
    private double value;
    private double change;
    private double changeRate;
}
