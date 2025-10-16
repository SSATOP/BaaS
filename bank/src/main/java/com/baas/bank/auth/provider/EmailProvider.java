package com.baas.bank.auth.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;  // Thymeleaf

    @Value("${spring.mail.rootUrl}")
    private String rootUrl;         // 서버 주소

    @Value("${spring.mail.username}")
    private String fromAddress;     // BaaS 이메일 주소

    @Value("${spring.mail.properties.mail.smtp.nickname}")
    private String fromName;        // BaaS

    private final String SUBJECT = "[SSATOP - BaaS 은행] 이메일 인증 메일입니다.";

    /**
     * 지정된 이메일 주소로 인증 토큰이 담긴 확인 메일을 비동기적으로 발송
     * AsyncConfig에 정의된 "mailExecutor" 스레드 풀을 사용하여 요청 스레드와 분리되어 실행
     *
     * @param email 수신자의 이메일 주소
     * @param code 사용자의 이메일 인증을 위한 고유 코드
     * @return CompletableFuture<Boolean> 메일 발송 성공 시 true, 실패 시 false를 포함하는 비동기 작업 결과 객체
     */
    @Async("mailExecutor")
    public CompletableFuture<Boolean> sendVerificationMail(String email, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            // Thymeleaf 컨텍스트 생성 및 변수 설정
            Context context = new Context();
            context.setVariable("email", email);
            context.setVariable("token", code);
            context.setVariable("rootUrl", rootUrl);
            String htmlContent = templateEngine.process("email-verification", context); // templates/email-verification.html

            // 이메일 기본 정보 설정
            messageHelper.setTo(email);     // 대상의 이메일 주소
            messageHelper.setSubject(SUBJECT);      // 제목
            messageHelper.setText(htmlContent, true);   // 내용
            messageHelper.setFrom(fromAddress, fromName);   // 별명 추가

            File logo = new File("src/main/resources/static/images/logo.png");
            messageHelper.addInline("logo", logo);

            javaMailSender.send(message);
            log.info("인증 메일 발송 성공 - 수신자: {}", email);
        } catch (Exception e) {
            // 메일 발송 과정에서 예외가 발생하면 로그를 기록하고, 이 비동기 작업이 실패했음을 알림
            log.error("인증 메일 발송 실패 - 수신자: {}, 에러 메시지: {}", email, e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        // 비동기 작업이 성공했음을 알림
        return CompletableFuture.completedFuture(true);
    }
}
