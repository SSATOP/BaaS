package com.baas.bank.transaction.controller;

import com.baas.bank.auth.security.CustomUserDetails;
import com.baas.bank.transaction.dto.TransactionCreateRequest;
import com.baas.bank.transaction.dto.TransactionCreateResponse;
import com.baas.bank.transaction.dto.TransactionDto;
import com.baas.bank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * 거래 생성 (입금/출금/이체)
     * POST /transactions?type=DEPOSIT, WITHDRAW, 또는 TRANSFER
     */
    @PostMapping
    public ResponseEntity<TransactionCreateResponse> createTransaction(
            @RequestParam String type,
            @RequestBody TransactionCreateRequest request) {
        
        Long userId = getCurrentUserId();
        
        if ("DEPOSIT".equalsIgnoreCase(type)) {
            TransactionCreateResponse response = transactionService.deposit(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if ("WITHDRAW".equalsIgnoreCase(type)) {
            TransactionCreateResponse response = transactionService.withdraw(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if ("TRANSFER".equalsIgnoreCase(type)) {
            TransactionCreateResponse response = transactionService.transfer(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 거래 내역 조회
     * GET /transactions?accountId={id}
     */
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam Long accountId) {
        
        Long userId = getCurrentUserId();
        
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(userId, accountId);
        return ResponseEntity.ok(transactions);
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

