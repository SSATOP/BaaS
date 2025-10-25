package com.baas.securities.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JwtDto {
    private String accessToken;
    private String refreshToken;
}
