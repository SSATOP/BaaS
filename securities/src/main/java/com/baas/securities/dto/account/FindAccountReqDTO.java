package com.baas.securities.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindAccountReqDTO {
    private String accountNumber;
    private String accountPassword;
}
