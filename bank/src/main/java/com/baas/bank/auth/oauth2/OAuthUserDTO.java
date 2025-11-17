package com.baas.bank.auth.oauth2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthUserDTO {

    private Long id;
    private String username;
}
