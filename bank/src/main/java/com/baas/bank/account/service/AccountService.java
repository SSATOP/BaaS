package com.baas.bank.account.service;

import com.baas.bank.account.dto.*;
import com.baas.bank.account.mapper.AccountMapper;
import com.baas.bank.account.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;
    private final PasswordEncoder passwordEncoder;
    
    private static final String DEFAULT_ALIAS = "입출금통장";
    private static final int ACCOUNT_EXPIRY_YEARS = 5; // 계좌 만료 기간 5년
    
    @Transactional
    public AccountCreateResponse createAccount(Long userId, AccountCreateRequest request) {
        // 계좌번호 생성 (중복 체크 포함)
        String accountNumber;
        int maxAttempts = 10;
        int attempts = 0;
        
        do {
            accountNumber = accountNumberGenerator.generateAccountNumber();
            attempts++;
            if (attempts >= maxAttempts) {
                throw new RuntimeException("계좌번호 생성에 실패했습니다. 다시 시도해주세요.");
            }
        } while (accountMapper.existsByAccountNumber(accountNumber));
        
        // 계좌 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // 별칭 설정 (없으면 기본값)
        String alias = (request.getAlias() != null && !request.getAlias().trim().isEmpty()) 
                ? request.getAlias().trim() 
                : DEFAULT_ALIAS;
        
        // 만료일 계산 (발급일로부터 5년)
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = issuedAt.plusYears(ACCOUNT_EXPIRY_YEARS);
        
        // AccountDto 생성
        AccountDto account = new AccountDto();
        account.setUserId(userId);
        account.setAccountNumber(accountNumber);
        account.setBankCode(request.getBankCode());
        account.setAlias(alias);
        account.setBalance(1000000L); // 기본 잔액 1,000,000원
        account.setPassword(encodedPassword);
        account.setIssuedAt(issuedAt);
        account.setExpiredAt(expiredAt);
        
        // DB 저장
        accountMapper.insertAccount(account);
        
        // 응답 생성
        return new AccountCreateResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBankCode(),
                account.getAlias(),
                account.getBalance(),
                account.getIssuedAt(),
                account.getExpiredAt()
        );
    }
    
    @Transactional
    public void updateAlias(Long userId, Long accountId, String alias) {
        // 계좌 존재 및 소유권 확인
        AccountDto account = accountMapper.findById(accountId);
        if (account == null) {
            throw new RuntimeException("계좌를 찾을 수 없습니다.");
        }
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("해당 계좌에 대한 권한이 없습니다.");
        }
        
        // 별칭 업데이트
        String updatedAlias = (alias != null && !alias.trim().isEmpty()) 
                ? alias.trim() 
                : DEFAULT_ALIAS;
        
        accountMapper.updateAlias(accountId, updatedAlias);
    }
    
    public AccountListResponse getAccountsByUserId(Long userId) {
        List<AccountDto> accounts = accountMapper.findByUserId(userId);
        
        if (accounts.isEmpty()) {
            return new AccountListResponse("NO_ACCOUNT", null);
        }
        
        List<AccountResponse> accountResponses = accounts.stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getBankCode(),
                        account.getAlias(),
                        account.getBalance(),
                        account.getIssuedAt(),
                        account.getExpiredAt()
                ))
                .collect(Collectors.toList());
        
        return new AccountListResponse("SUCCESS", accountResponses);
    }
}

