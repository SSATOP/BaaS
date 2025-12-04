package com.baas.bank.user.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;                // BIGINT AUTO_INCREMENT
    private String name;            // name
    private LocalDate birthDate;    // birth_date
    private String phone;           // phone
    private String email;           // email
    private String oauthProvider;// oauth_provider
    private String oauthId;// oauth_id
    private LocalDateTime createdAt;// created_at

}
