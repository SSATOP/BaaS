package com.baas.securities.dto.email;

import com.baas.securities.repository.entity.EmailValidation;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailValidationResDTO {
    private String id;
    private LocalDateTime expiresAt;
    private String code;

    public static EmailValidationResDTO generateResDTO(EmailValidation emailValidation) {
        return EmailValidationResDTO.builder()
                .id(emailValidation.getId())
                .code(emailValidation.getCode())
                .expiresAt(emailValidation.getExpiresAt())
                .build();
    }
}
