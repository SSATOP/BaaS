package com.baas.oauth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Consent {
    private String registeredClientId;
    private String principalName;
    private String authorities;
}