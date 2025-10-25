package com.baas.securities.repository.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailValidation {
    private String id;
    private String code;
    private LocalDateTime expiresAt;
    private boolean isVerified;
    private LocalDateTime verifiedAt;
    private String email;

    @Builder
    public EmailValidation(String code, LocalDateTime expiresAt, boolean isVerified, LocalDateTime verifiedAt, String email) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.isVerified = isVerified;
        this.verifiedAt = verifiedAt;
        this.email = email;
        this.id = UUID.randomUUID().toString();
    }
}
