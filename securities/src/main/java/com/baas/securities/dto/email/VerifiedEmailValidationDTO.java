package com.baas.securities.dto.email;

import com.baas.securities.repository.entity.EmailValidation;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifiedEmailValidationDTO {
    private String transactionId;
    private boolean verified;
    private LocalDateTime verifiedAt;

    public static VerifiedEmailValidationDTO generate(EmailValidation validation) {
        return VerifiedEmailValidationDTO.builder()
                .transactionId(validation.getId())
                .verified(validation.isVerified())
                .verifiedAt(validation.getVerifiedAt())
                .build();
    }
}
