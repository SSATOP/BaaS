package com.baas.securities.dto.agreement;

import com.baas.securities.repository.entity.UserAgreement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UserAgreementResDTO {
    private String agreementId;
    private LocalDateTime createdAt;

    public static UserAgreementResDTO generate(UserAgreement userAgreement) {
        return UserAgreementResDTO.builder()
                .agreementId(userAgreement.getId())
                .createdAt(userAgreement.getCreatedAt())
                .build();
    }
}
