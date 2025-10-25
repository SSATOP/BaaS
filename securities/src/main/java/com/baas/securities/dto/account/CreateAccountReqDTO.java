package com.baas.securities.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountReqDTO {
    private Long initialDeposit;
    private String password;
    private String accountType;
}
