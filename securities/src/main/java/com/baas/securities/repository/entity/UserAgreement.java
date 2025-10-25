package com.baas.securities.repository.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAgreement {
    private String id;
    private String userId;
    private boolean termOfService;
    private boolean privacyPolicy;
    private boolean marketing;
    private LocalDateTime createdAt;

    @Builder
    public UserAgreement(String userId, boolean termOfService, boolean privacyPolicy, boolean marketing, LocalDateTime createdAt) {
        this.userId = userId;
        this.termOfService = termOfService;
        this.privacyPolicy = privacyPolicy;
        this.marketing = marketing;
        this.createdAt = createdAt;
        this.id = UUID.randomUUID().toString();
    }
}
