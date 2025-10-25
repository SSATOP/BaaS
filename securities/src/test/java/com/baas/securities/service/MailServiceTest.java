package com.baas.securities.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Test
    @DisplayName("메일 전송 테스트")
    void mailSendTest() {
        // given
        int number = mailService.sendMail("user2@gmail.com");
        // when
        System.out.println(number);
        // then
    }
}