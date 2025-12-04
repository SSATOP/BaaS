package com.baas.bank.account.controller;

import com.baas.bank.account.dto.*;
import com.baas.bank.account.service.AccountService;
import com.baas.bank.auth.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * 계좌 개설
     * POST /accounts
     */
    @PostMapping
    public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody AccountCreateRequest request) {
        Long userId = getCurrentUserId();

        AccountCreateResponse response = accountService.createAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 계좌 별칭 등록/수정
     * PUT /accounts/{id}/alias
     */
    @PutMapping("/{id}/alias")
    public ResponseEntity<String> updateAlias(
            @PathVariable Long id,
            @RequestBody AliasUpdateRequest request) {
        Long userId = getCurrentUserId();
        
        accountService.updateAlias(userId, id, request.getAlias());
        return ResponseEntity.ok("별칭이 업데이트되었습니다.");
    }
    
    /**
     * 계좌 목록 조회
     * GET /accounts
     */
    @GetMapping
    public ResponseEntity<AccountListResponse> getAccounts() {
        Long userId = getCurrentUserId();
        
        AccountListResponse response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 현재 로그인한 사용자의 ID를 가져옵니다.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }
}

