package com.baas.bank;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScans({
    @MapperScan("com.baas.bank.user.mapper"), // 사용자 매퍼 경로
    @MapperScan("com.baas.bank.account.mapper") // 계좌 매퍼 경로
})
public class BankApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}
