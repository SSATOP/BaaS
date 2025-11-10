package com.baas.oauth.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class User {
    private Long id;
    private String userId;
    private String password;
    private String userRole;
    private String username;
    private String email;
    private String gender;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}