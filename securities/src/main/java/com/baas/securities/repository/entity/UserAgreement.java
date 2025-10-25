package com.baas.securities.repository.entity;

import com.baas.securities.dto.agreement.UserAgreementReqDTO;
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
    private boolean termsOfService;
    private boolean privacyPolicy;
    private boolean marketing;
    private LocalDateTime createdAt;

    @Builder
    public UserAgreement(String userId, boolean termsOfService, boolean privacyPolicy, boolean marketing, LocalDateTime createdAt) {
        this.userId = userId;
        this.termsOfService = termsOfService;
        this.privacyPolicy = privacyPolicy;
        this.marketing = marketing;
        this.createdAt = createdAt;
        this.id = UUID.randomUUID().toString();
    }

    public void update(UserAgreementReqDTO dto) {
        this.privacyPolicy = dto.isPrivacyPolicy();
        this.marketing = dto.isMarketing();
        this.termsOfService = dto.isTermsOfService();
    }
}
