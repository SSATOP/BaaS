package com.baas.bank.auth.service;

import com.baas.bank.auth.entity.EmailVerify;
import com.baas.bank.auth.provider.EmailProvider;
import com.baas.bank.auth.repository.EmailVerifyRepository;
import com.baas.bank.global.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailProvider emailProvider;
    private final EmailVerifyRepository emailVerifyRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public void send(String email) {
        CompletableFuture<EmailVerify> future = emailProvider.sendVerificationMail(email, codeGenerator.generateCode());
        future.thenAccept(verify -> {
            if (verify != null) {
                emailVerifyRepository.save(verify);
            }
            // TODO 에러 처리
        });
    }

    // 이메일과 인증 코드가 일치하는지 검증
    public boolean verifyEmail(String email, String code) {
        // Redis에서 이메일(ID)을 기준으로 인증 정보를 조회
        Optional<EmailVerify> optionalEmailVerify = emailVerifyRepository.findById(email);

        // 인증 시간이 만료되었거나, 요청한 적이 없는 경우
        if (optionalEmailVerify.isEmpty()) {
            System.out.println("인증 정보 없음 또는 만료됨: " + email);
            // TODO 에러 처리
            return false;
        }

        // 조회된 정보가 있는 경우
        EmailVerify foundVerification = optionalEmailVerify.get();
        if (foundVerification.getCode().equals(code)) {
            System.out.println("인증 성공: " + email);
            emailVerifyRepository.deleteById(email);
            return true;
        } else {
            System.out.println("인증 코드 불일치: " + email);
            // TODO 에러 처리
            return false;
        }
    }
}
