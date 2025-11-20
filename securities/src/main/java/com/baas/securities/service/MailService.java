package com.baas.securities.service;

import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.dto.email.EmailValidationResDTO;
import com.baas.securities.dto.email.VerifiedEmailValidationDTO;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.BadRequestException;
import com.baas.securities.exception.ex.InternalServerErrorException;
import com.baas.securities.exception.ex.NotFoundException;
import com.baas.securities.repository.EmailValidationRepository;
import com.baas.securities.repository.entity.EmailValidation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
/**
 * db 이벤트 스케줄러로 expires_at 이 지났으면 db에서 제거
 */
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailValidationRepository validationRepository;

    private final int EXPIRES_TIME = 5;

    public EmailValidationResDTO sendMail(String mail) throws MessagingException {
        // 1. 인증코드 무작위 생성, message 생성
        int number = createNumber();
        MimeMessage message;
        try {
            message = createMail(mail, number);
        }catch (MessagingException e) {
            log.error("Failed to create MimeMessage for email: {}", mail, e);
            throw new InternalServerErrorException(ErrorCode.EMAIL_SEND_FAILED);
        }

        // 2. EmailValidation 객체 생성
        EmailValidation emailValidation = generateEmailValidation(mail, number);

        try {
            // 3. 인증 메일 발송
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", mail, e);
            // 500 에러: EMAIL_SEND_FAILED (메일 발송 실패)
            throw new InternalServerErrorException(ErrorCode.EMAIL_SEND_FAILED);
        }


        // 4. emailValidation 객체 db 저장
        validationRepository.save(emailValidation);

        // EmailValidationResDTO 객체 만들어 반환
        return EmailValidationResDTO.generateResDTO(emailValidation);
    }

    public VerifiedEmailValidationDTO verify(String id, String code)  {
        // 1. 존재 여부 체크
        EmailValidation emailValidation = validationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EMAIL_STATUS_NOT_FOUND));

        // 2. 인증 기간이 지났는지, 이미 인증된 코드인지, 코드가 일치하는지 체크
        verifyValidationByExpiresAt(emailValidation);
        isVerifyAlready(emailValidation);

        verifyValidationByCode(emailValidation, code);

        emailValidation.verify();
        // db 업데이트
        validationRepository.updateIsAndAtVerified(emailValidation);

        return VerifiedEmailValidationDTO.generate(emailValidation);
    }

    private MimeMessage createMail(String mail, int number) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        log.info("CREATE MAIL METHOD START TRY CATCH");
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>" + "감사합니다." + "</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public void validate(AuthUser user, String email) {
        if (!user.getEmail().equals(email)) {
            throw new BadRequestException(ErrorCode.NOT_MATCH_USER_EMAIL);
        }
    }

    private int createNumber() {
        return (int) (Math.random() * 90000) + 100000;
    }

    private EmailValidation generateEmailValidation(String email, int code) {
        return EmailValidation.builder()
                .code(String.valueOf(code))
                .email(email)
                .isVerified(false)
                .expiresAt(getExpireLocalDateTime())
                .build();
    }

    // 5분 동안 인증
    private LocalDateTime getExpireLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusMinutes(EXPIRES_TIME);
    }

    private void verifyValidationByCode(EmailValidation validation, String code) {
        if (!validation.getCode().equals(code)) {
            throw new BadRequestException(ErrorCode.INVALID_OR_EXPIRED_CODE);
        }
    }

    private void verifyValidationByExpiresAt(EmailValidation validation) {
        LocalDateTime now = LocalDateTime.now();
        if (validation.getExpiresAt().isBefore(now)) {
            throw new BadRequestException(ErrorCode.INVALID_OR_EXPIRED_CODE);
        }
    }

    private void isVerifyAlready(EmailValidation validation) {
        if (validation.isVerified()) {
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }
    }
}
