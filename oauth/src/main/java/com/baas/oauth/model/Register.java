package com.baas.oauth.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Register {

    private String id;
    private String clientId;
    private Instant clientIdIssuedAt;
    private String clientSecret;
    private Instant clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String postLogoutRedirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;

}