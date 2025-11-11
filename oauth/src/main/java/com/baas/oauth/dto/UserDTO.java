package com.baas.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDTO {
    private String userId;
    private String password;
    private String userRole;
    private String username;
    private String email;
    private String gender;
    private String phone;
}
