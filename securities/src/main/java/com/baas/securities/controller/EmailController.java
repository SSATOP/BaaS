package com.baas.securities.controller;

import com.baas.securities.dto.*;
import com.baas.securities.security.resolver.Login;
import com.baas.securities.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification/email")
public class EmailController {

    private final MailService mailService;

    /**
     * 기본 흐름
     * 1. 요청 바디 이메일과 로그인 유저의 이메일 비교
     * 2. 메일 발송
     * 3. 메일 발송 정보를 응답 객체에 담아 반환.
     */
    @PostMapping("/request")
    public ResponseDTO sendMailToUserEmail(@Login AuthUser authUser, @RequestBody String email) throws MessagingException {
        // 로그인 된 유저와 요청 메시지의 이메일이 일치하는지 확인하는 메소드
        mailService.validate(authUser, email);
        // 메일 발송 메소드
        EmailValidationResDTO dto = mailService.sendMail(email);

        return new ResponseDTO(HttpStatus.CREATED, "인증 이메일이 전송되었습니다.", dto);
    }

    /**
     * 기본 흐름
     * 1. 요청 바디 이메일과 로그인 유저의 이메일 비교
     * 2. 코드 확인
     * 3. 확인 결과 반환.
     */
    @PostMapping("/confirm")
    public ResponseDTO verifyMailCode(@Login AuthUser authUser, @RequestBody EmailValidationVerifyDTO dto) {
        mailService.validate(authUser, dto.getEmail());

        VerifiedEmailValidationDTO verified = mailService.verify(dto.getTransactionId(), dto.getCode());

        return new ResponseDTO(HttpStatus.OK, "이메일 인증이 완료되었습니다.", verified);
    }
}
