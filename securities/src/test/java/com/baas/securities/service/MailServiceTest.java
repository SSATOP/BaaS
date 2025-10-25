package com.baas.securities.service;

import com.baas.securities.dto.EmailValidationResDTO;
import com.baas.securities.dto.VerifiedEmailValidationDTO;
import com.baas.securities.repository.EmailValidationRepository;
import com.baas.securities.repository.entity.EmailValidation;
import jakarta.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private EmailValidationRepository repository;

    @Test
    @DisplayName("메일 전송 테스트")
    void mailSendTest() throws MessagingException {
        // given
        EmailValidationResDTO dto = mailService.sendMail("user2@gmail.com");
        // when
        System.out.println(dto.getCode());
        // then
    }

    @Test
    @DisplayName("인증 진행")
    void findValidationInfo() {
        // given
        EmailValidation emailValidation = repository.findFirstByEmail("user2@gmail.com").orElseThrow(IllegalAccessError::new);
        // when
        System.out.println(emailValidation.getId() + ", " + emailValidation.getCode());
        VerifiedEmailValidationDTO verified = mailService.verify("6aa1ce4e-1326-4a35-bb79-ec885592b009", "139736");
        // then
        System.out.println(verified.getTransactionId());
    }
}