package com.baas.bank.auth.service;

import com.baas.bank.auth.entity.EmailVerify;
import com.baas.bank.auth.provider.EmailProvider;
import com.baas.bank.auth.repository.EmailVerifyRepository;
import com.baas.bank.global.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        });
    }
}
