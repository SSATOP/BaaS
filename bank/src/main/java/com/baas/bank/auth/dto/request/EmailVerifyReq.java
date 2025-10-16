package com.baas.bank.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyReq {
    private String email;
    private String code;
}
