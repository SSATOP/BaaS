package com.baas.bank.auth.controller;

import com.baas.bank.auth.dto.request.EmailSendReq;
import com.baas.bank.auth.dto.request.EmailVerifyReq;
import com.baas.bank.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/email/send")
    ResponseEntity<String> send(@RequestBody EmailSendReq email) {
        authService.send(email.getEmail());
        return ResponseEntity.ok("ok");
    }
}
