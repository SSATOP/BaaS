package com.baas.securities.repository;

import com.baas.securities.repository.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("find by email and acc number")
    void emailAndAccNum() {
        // given


        // when
        Account account = accountRepository
                .findByEmailAndAccountNumber("user2@gmail.com", "867-859-577865")
                .orElseThrow(IllegalAccessError::new);
        // then
        System.out.println(account.getId());
    }

}