package com.baas.securities.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtDto {
    private String accessToken;
    private String refreshToken;
}
