package com.baas.securities.dto.security;

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
