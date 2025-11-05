package com.baas.securities.dto.agreement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserAgreementReqDTO {
    private boolean termsOfService;
    private boolean marketing;
    private boolean privacyPolicy;
}
