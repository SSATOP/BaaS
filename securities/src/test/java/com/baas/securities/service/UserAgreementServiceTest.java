package com.baas.securities.service;

import com.baas.securities.dto.agreement.UserAgreementReqDTO;
import com.baas.securities.dto.agreement.UserAgreementResDTO;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.repository.UserAgreementRepository;
import com.baas.securities.repository.entity.UserAgreement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAgreementServiceTest {

    @Autowired
    UserAgreementService service;
    @Autowired
    UserAgreementRepository repository;

    @Test
    @DisplayName("동의 정보 저장 테스트")
    void saveTest() {
        // given
        UserAgreementReqDTO agreement = new UserAgreementReqDTO();
        agreement.setMarketing(true);
        agreement.setTermsOfService(true);
        agreement.setPrivacyPolicy(true);

        AuthUser user = AuthUser.builder()
                .email("user2@gmail.com")
                .build();

        UserAgreementResDTO saved = service.save(user, agreement);
        System.out.println(saved.getAgreementId());
        // when

        // then
    }

    @Test
    @DisplayName("update test")
    void updateTest() {
        // given
        UserAgreementReqDTO agreement = new UserAgreementReqDTO();
        agreement.setTermsOfService(false);

        AuthUser user = AuthUser.builder()
                .email("user2@gmail.com")
                .build();
        // when
        service.update(user, agreement);
        // then
    }
}