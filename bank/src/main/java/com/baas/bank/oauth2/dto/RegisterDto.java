package com.baas.bank.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {

    private String clientName;
    private String clientSecret;
    private String redirectUris;
    private String postLogoutRedirectUris;
    private String scopes;
}
