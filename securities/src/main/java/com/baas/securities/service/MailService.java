package com.baas.securities.service;

import com.baas.securities.dto.AuthUser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public int sendMail(String mail) {
        int number = createNumber();
        MimeMessage message = createMail(mail, number);
        mailSender.send(message);

        return number;
    }

    private MimeMessage createMail(String mail, int number) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            log.info("CREATE MAIL METHOD START TRY CATCH");
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public void validate(AuthUser user, String email) {
        if (!user.getEmail().equals(email)) {
            throw new IllegalArgumentException("가입된 이메일이 아닙니다. 확인해주세요!");
        }
    }

    private int createNumber() {
        return (int) (Math.random() * 90000) + 100000;
    }
}
