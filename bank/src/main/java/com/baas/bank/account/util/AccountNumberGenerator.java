package com.baas.bank.account.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {
    
    private static final Random RANDOM = new Random();
    private static final String DIGITS = "0123456789";
    
    /**
     * 계좌번호를 생성합니다.
     * 형식: 12자리 숫자 (예: 123456789012)
     * 
     * @return 생성된 계좌번호
     */
    public String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int index = RANDOM.nextInt(DIGITS.length());
            accountNumber.append(DIGITS.charAt(index));
        }
        return accountNumber.toString();
    }
}

