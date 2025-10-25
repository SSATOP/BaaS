package com.baas.securities.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailValidationVerifyDTO {
    private String transactionId;
    private String email;
    private String code;
}
