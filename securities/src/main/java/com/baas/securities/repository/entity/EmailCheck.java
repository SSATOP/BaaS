package com.baas.securities.repository.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheck {
    private String id;
    private String code;
    private LocalDateTime expiredAt;
    private boolean isVerified;
    private LocalDateTime verifiedAt;
    private String email;

    @Builder
    public EmailCheck(String code, LocalDateTime expiredAt, boolean isVerified, LocalDateTime verifiedAt, String email) {
        this.code = code;
        this.expiredAt = expiredAt;
        this.isVerified = isVerified;
        this.verifiedAt = verifiedAt;
        this.email = email;
        this.id = UUID.randomUUID().toString();
    }
}
