package com.baas.securities.repository.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String oauthProvider;
    private String oauthId;
    private String name;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String role;

    @Builder
    public User(String email, String oauthProvider, String oauthId, String name, String phoneNumber, LocalDateTime birthDate, LocalDateTime createdAt, LocalDateTime lastLogin, String role) {
        this.email = email;
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.role = role;
        id = UUID.randomUUID().toString();
    }
}
