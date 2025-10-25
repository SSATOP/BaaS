package com.baas.securities.service;

import com.baas.securities.dto.account.CreateAccountReqDTO;
import com.baas.securities.dto.account.CreateAccountResDTO;
import com.baas.securities.dto.account.FindAccountReqDTO;
import com.baas.securities.dto.account.FindAccountResDTO;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    AccountService service;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("계좌 생성")
    void createTest() {
        // given
        CreateAccountReqDTO dto = new CreateAccountReqDTO();
        dto.setAccountType("SECURITIES");
        dto.setPassword("1234");
        dto.setInitialDeposit(10000000L);

        AuthUser user = AuthUser.builder()
                .email("user2@gmail.com")
                .build();

        // when
        CreateAccountResDTO account = service.createAccount(user, dto);
        // then
    }

    @Test
    @DisplayName("계좌 조회 테스트")
    void accountFindTest() throws IllegalAccessException {
        // given
        AuthUser user = AuthUser.builder()
                .email("user2@gmail.com")
                .build();

        FindAccountReqDTO dto = new FindAccountReqDTO();
        dto.setAccountNumber("867-859-577865");
        dto.setAccountPassword("1234");

        FindAccountResDTO findAccount = accountService.findByAccountNumber(user, dto);
        // when
        System.out.println(findAccount.getAccountNumber() + ", " + findAccount.getBalance());
        // then
    }

}