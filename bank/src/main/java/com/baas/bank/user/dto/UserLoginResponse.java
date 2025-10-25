package com.baas.bank.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserLoginResponse {
    private Long userId;
    private String loginId;
    private String name;
    private String message;

}
