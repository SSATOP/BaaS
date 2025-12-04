package com.baas.bank.transaction.service;

import com.baas.bank.account.dto.AccountDto;
import com.baas.bank.account.mapper.AccountMapper;
import com.baas.bank.transaction.dto.TransactionCreateRequest;
import com.baas.bank.transaction.dto.TransactionCreateResponse;
import com.baas.bank.transaction.dto.TransactionDto;
import com.baas.bank.transaction.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    
    // 거래 유형 상수
    public static final Long TRANSACTION_TYPE_DEPOSIT = 1L;  // 입금
    public static final Long TRANSACTION_TYPE_WITHDRAW = 2L; // 출금
    public static final Long TRANSACTION_TYPE_TRANSFER = 3L;  // 이체
    
    // 최소/최대 입금 금액 제한
    private static final Long MIN_DEPOSIT_AMOUNT = 1L;
    private static final Long MAX_DEPOSIT_AMOUNT = 100000000L; // 1억원
    
    // 최소/최대 출금 금액 제한
    private static final Long MIN_WITHDRAW_AMOUNT = 1L;
    private static final Long MAX_WITHDRAW_AMOUNT = 100000000L; // 1억원
    
    @Transactional
    public TransactionCreateResponse deposit(Long userId, TransactionCreateRequest request) {
        // 1. 계좌 존재 및 소유권 확인
        AccountDto account = accountMapper.findById(request.getAccId());
        if (account == null) {
            throw new RuntimeException("계좌를 찾을 수 없습니다.");
        }
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("해당 계좌에 대한 권한이 없습니다.");
        }
        
        // 2. 금액 검증
        validateDepositAmount(request.getAmount());
        
        // 3. 잔액 증가
        Long currentBalance = account.getBalance();
        Long newBalance = currentBalance + request.getAmount();
        
        // 4. 계좌 잔액 업데이트
        accountMapper.updateBalance(request.getAccId(), newBalance);
        
        // 5. 거래 내역 저장
        TransactionDto transaction = new TransactionDto();
        transaction.setAccId(request.getAccId());
        transaction.setTargetAccId(null);  // 입금은 대상 계좌 없음
        transaction.setTargetAccBankcode(null);
        transaction.setType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setTxDatetime(LocalDateTime.now());
        transaction.setMemo(request.getMemo());
        transaction.setCreatedAt(LocalDateTime.now());
        
        transactionMapper.insertTransaction(transaction);
        
        // 6. 응답 생성
        return new TransactionCreateResponse(
                transaction.getId(),
                transaction.getAccId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getTxDatetime(),
                transaction.getMemo()
        );
    }
    
    @Transactional
    public TransactionCreateResponse withdraw(Long userId, TransactionCreateRequest request) {
        // 1. 계좌 존재 및 소유권 확인
        AccountDto account = accountMapper.findById(request.getAccId());
        if (account == null) {
            throw new RuntimeException("계좌를 찾을 수 없습니다.");
        }
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("해당 계좌에 대한 권한이 없습니다.");
        }
        
        // 2. 계좌 비밀번호 검증
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("계좌 비밀번호를 입력해주세요.");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("계좌 비밀번호가 일치하지 않습니다.");
        }
        
        // 3. 금액 검증
        validateWithdrawAmount(request.getAmount());
        
        // 4. 잔액 확인
        Long currentBalance = account.getBalance();
        if (currentBalance < request.getAmount()) {
            throw new RuntimeException("잔액이 부족합니다.");
        }
        
        // 5. 잔액 감소
        Long newBalance = currentBalance - request.getAmount();
        
        // 6. 계좌 잔액 업데이트
        accountMapper.updateBalance(request.getAccId(), newBalance);
        
        // 7. 거래 내역 저장
        TransactionDto transaction = new TransactionDto();
        transaction.setAccId(request.getAccId());
        transaction.setTargetAccId(null);  // 출금은 대상 계좌 없음
        transaction.setTargetAccBankcode(null);
        transaction.setType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setTxDatetime(LocalDateTime.now());
        transaction.setMemo(request.getMemo());
        transaction.setCreatedAt(LocalDateTime.now());
        
        transactionMapper.insertTransaction(transaction);
        
        // 8. 응답 생성
        return new TransactionCreateResponse(
                transaction.getId(),
                transaction.getAccId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getTxDatetime(),
                transaction.getMemo()
        );
    }
    
    /**
     * 입금 금액 검증
     */
    private void validateDepositAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("입금 금액은 0보다 커야 합니다.");
        }
        
        if (amount < MIN_DEPOSIT_AMOUNT) {
            throw new RuntimeException(String.format("입금 금액은 최소 %d원 이상이어야 합니다.", MIN_DEPOSIT_AMOUNT));
        }
        
        if (amount > MAX_DEPOSIT_AMOUNT) {
            throw new RuntimeException(String.format("입금 금액은 최대 %d원을 초과할 수 없습니다.", MAX_DEPOSIT_AMOUNT));
        }
    }
    
    /**
     * 출금 금액 검증
     */
    private void validateWithdrawAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("출금 금액은 0보다 커야 합니다.");
        }
        
        if (amount < MIN_WITHDRAW_AMOUNT) {
            throw new RuntimeException(String.format("출금 금액은 최소 %d원 이상이어야 합니다.", MIN_WITHDRAW_AMOUNT));
        }
        
        if (amount > MAX_WITHDRAW_AMOUNT) {
            throw new RuntimeException(String.format("출금 금액은 최대 %d원을 초과할 수 없습니다.", MAX_WITHDRAW_AMOUNT));
        }
    }
}

