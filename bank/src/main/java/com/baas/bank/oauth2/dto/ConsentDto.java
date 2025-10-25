package com.baas.bank.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsentDto {
    private String registeredClientId;
    private String principalName;
    private String authorities;
}