package com.baas.bank.transaction.controller;

import com.baas.bank.transaction.dto.TransactionCreateRequest;
import com.baas.bank.transaction.dto.TransactionCreateResponse;
import com.baas.bank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * 입금 (가상 충전)
     * POST /transactions?type=DEPOSIT
     */
    @PostMapping
    public ResponseEntity<TransactionCreateResponse> createTransaction(
            @RequestParam String type,
            @RequestBody TransactionCreateRequest request) {
        
        if (!"DEPOSIT".equalsIgnoreCase(type)) {
            return ResponseEntity.badRequest().build();
        }
        
        TransactionCreateResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

