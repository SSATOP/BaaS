package com.baas.securities.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
public class Account {
    private String id; // 계좌 아이디
    private String userId;  // 사용자 아이디
    private String accountNumber;  // 계좌 번호
    private String accountPassword;// 계좌 비밀번호
    private Long balance;        // 계좌 잔고
    private boolean isActive;          // 활성 여부
    private String accountType;        // 계좌 타입
    private LocalDateTime createdAt;   // 생성일
    private LocalDateTime lastUpdate;  // 마지막 수정일

    @Builder
    public Account(String userId, String accountNumber, Long balance, String accountPassword, String accountType, boolean isActive, LocalDateTime createdAt, LocalDateTime lastUpdate) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountPassword = accountPassword;
        this.accountType = accountType;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        id = UUID.randomUUID().toString();
    }
    public void updateBalance(Long balance){
        this.balance=balance;

    }


    public static Account createDummyAccount() {
        return Account.builder()
                .accountNumber(generateRandomAccountNumber())
                .accountPassword(generateRandomPassword())
                .balance((long) (Math.random() * 10_000_000))
                .isActive(true)
                .build();
    }

    private static String generateRandomAccountNumber() {
        int part1 = (int) (Math.random() * 900) + 100;
        int part2 = (int) (Math.random() * 900) + 100;
        int part3 = (int) (Math.random() * 900000) + 100000;
        return part1 + "-" + part2 + "-" + part3;
    }

    private static String generateRandomPassword() {
        int pw = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(pw);
    }
}
