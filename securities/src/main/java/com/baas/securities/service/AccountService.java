package com.baas.securities.service;

import com.baas.securities.dto.account.*;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.repository.AccountRepository;
import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.Account;
import com.baas.securities.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public CreateAccountResDTO createAccount(AuthUser user, CreateAccountReqDTO dto) {
        // 1. 유저 확인.
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 계좌 생성.
        Account account = generateAccount(findUser, dto);
        // 3. 생성한 계좌 db에 저장.
        accountRepository.save(account);

        return CreateAccountResDTO.generate(account);
    }

    public FindAllAccountResDTO findAllByUserEmail(AuthUser user) {
        // 이메일로 유저의 계좌 찾기
        List<Account> allAccount = accountRepository.findAllByEmail(user.getEmail());

        // 응답 dto에 찾아온 데이터 파싱해서 담기
        FindAllAccountResDTO resDto = new FindAllAccountResDTO();

        allAccount.stream().forEach(account -> {
            resDto.getAccounts().add(FindAccountResDTO.generate(account));
        });

        return resDto;
    }

    public FindAccountResDTO findByAccountNumber(AuthUser user, FindAccountReqDTO dto) throws IllegalAccessException {
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾지 못했습니다."));

        // 존재하는 계좌인지 확인.
        Account findAccount = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("없는 계좌번호입니다."));

        // 계좌 비밀번호가 일치하는지 확인.
        validatePassword(findAccount, dto.getAccountPassword());

        return FindAccountResDTO.generate(findAccount);
    }

    private void isUserAccount(User user, Account account) {
        if (!user.getId().equals(account.getUserId())) {
            throw new IllegalArgumentException("요청한 사용자의 계좌가 아닙니다.");
        }
    }

    private void validatePassword(Account account, String password) throws IllegalAccessException {
        if (!account.getAccountPassword().equals(password)) {
            throw new IllegalAccessException("계좌 비밀번호가 잘못되었습니다.");
        }
    }

    private Account generateAccount(User user, CreateAccountReqDTO dto) {
        String accountNumber = null;

        while (true) {
            accountNumber = Account.generateRandomAccountNumber();
            if (!isExistsAccountNumber(accountNumber)) {
                break;
            }
        }

        return Account.builder()
                .accountNumber(accountNumber)
                .accountPassword(dto.getPassword())
                .accountType(dto.getAccountType())
                .balance(new BigDecimal(String.valueOf(dto.getInitialDeposit())))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .userId(user.getId())
                .build();
    }

    // 계좌 번호 중복 판단.
    private boolean isExistsAccountNumber(String number) {
        return accountRepository.findByAccountNumber(number).isPresent();
    }
}
