package com.baas.securities.service;

import com.baas.securities.dto.AccIdUserIdInfoDTO;
import com.baas.securities.dto.account.*;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.enums.TransactionStatus;
import com.baas.securities.enums.TransactionType;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.BadRequestException;
import com.baas.securities.exception.ex.ForbiddenException;
import com.baas.securities.exception.ex.NotFoundException;
import com.baas.securities.exception.ex.UnauthorizedException;
import com.baas.securities.repository.AccountRepository;
import com.baas.securities.repository.TransactionRepository;
import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.Account;
import com.baas.securities.repository.entity.Transaction;
import com.baas.securities.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AbstractMessageSendingTemplate abstractMessageSendingTemplate;
    /**
     *  송금 로직
     */

    @Transactional
    public TransferResDTO transfer(AuthUser user, TransferReqDTO dto) {
        // 1. 보내는 유저/ 계좌 확인
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(()-> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // todo : fromAccountNumber가 없으면 토큰 유저의 기본 계좌등을 찾는 로직 필요
        // 요청이 있다고 가정
        Account fromAccount = accountRepository.findByAccountNumber(dto.getFromAccountNumber())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 2. 본인 계좌 확인
        isUserAccount(findUser, fromAccount);

        // 3. 이체 비밀 번호 확인
        validatePassword(fromAccount, dto.getTransferPassword());

        // 4. 받는 계좌 확인
        Account toAccount = accountRepository.findByAccountNumber(dto.getToAccountNumber())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));


        // 5. 자기 자신에게 송금하는 경우 방지
        if(fromAccount.getId().equals(toAccount.getId())){
            throw new BadRequestException(ErrorCode.SELF_TRANSFER_NOT_ALLOWED);
        }

        // 6. 잔액 확인
        if (fromAccount.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BadRequestException(ErrorCode.INSUFFICIENT_BALANCE_TRANSACTION);
        }

        // 7. 잔액 변경
        fromAccount.updateBalance(fromAccount.getBalance().subtract(dto.getAmount()));
        toAccount.updateBalance(toAccount.getBalance().add(dto.getAmount()));

        // 8. 변경된 잔액 DB에 반영
        accountRepository.update(fromAccount);
        accountRepository.update(toAccount);

        // 9. 거래 내역 기록 (출금, 입금 둘다)
        saveTransferTransaction(fromAccount, toAccount, dto.getAmount());

        log.info("송금 완료: {} -> {}, 금액: {}", fromAccount.getAccountNumber(), toAccount.getAccountNumber(), dto.getAmount());

        // 10. 응답 DTO 반환
        return new TransferResDTO(fromAccount.getId(), toAccount.getId(), dto.getAmount(), fromAccount.getBalance());

    }

    // 송금 거래 기록 저장 로직(출금.입금 트랜잭션 2개 생성)
    private void saveTransferTransaction(Account fromAccount, Account toAccount, BigDecimal amount) {
        // 출금 기록 (보내는 사람 기준)
        Transaction withdrawal = Transaction.builder()
                .accountId(fromAccount.getId())
                .transactionType(TransactionType.WITHDRAW) // 출금 타입
                .amount(amount.negate()) // 금액은 음수로 기록 (선택사항)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .toAccountId(toAccount.getId()) // 상대방 계좌 ID 기록
                // .toAccountType(toAccount.getAccountType()) // 필요하다면 타입도 기록
                .build();
        transactionRepository.save(withdrawal);

        // 입금 기록 (받는 사람 기준)
        Transaction deposit = Transaction.builder()
                .accountId(toAccount.getId())
                .transactionType(TransactionType.DEPOSIT) // 입금 타입
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                // .fromAccountId(fromAccount.getId()) // 보낸 사람 ID 기록 (필요하다면)
                .build();
        transactionRepository.save(deposit);
    }
    /**
     * 입출금 로직
     */
    public TransactionResDTO processTransaction(AuthUser user,String accountId, TransactionReqDTO dto) {
        // 1. 유저 확인
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. 계좌 확인
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 3. 본인 계좌 확인
        isUserAccount(findUser, account);

        // 4. 트랜잭션 타입에 따라 입급 또는 출금 처리
        // todo : 증권 계좌는 단지 입금 혹은 송금만 있는것이니 논의 필요
        switch (dto.getTransactionType()) {
            case WITHDRAW :
                withdraw(account, dto.getAmount());
                break;
            case DEPOSIT:
                deposit(account,dto.getAmount());
                break;
            default :
                throw new BadRequestException(ErrorCode.INVALID_TRANSACTION_TYPE);

        }

        // 5. 거래 내역 기록
        saveTransaction(account,dto.getTransactionType(),dto.getAmount());

        // 6. 응답 DTO 반환
        return new TransactionResDTO(account.getId(), account.getBalance());

    }

    public AccIdUserIdInfoDTO validateAccountByEmailAndAccountNumber(String email, String accountNumber) {
        Account account = accountRepository.findByEmailAndAccountNumber(email, accountNumber)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));

        return AccIdUserIdInfoDTO.generate(account);
    }


    // 입금 로직
    private void deposit(Account account, BigDecimal amount) {
        account.updateBalance(account.getBalance().add(amount));
        accountRepository.update(account);
        log.info("입금 완료. 계좌 ID: {}, 현재 잔액: {}", account.getId(), account.getBalance());
    }

    // 출금 로직
    private void withdraw(Account account, BigDecimal amount) {
        // 출금 가능 잔액 부족 예외 처리
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException(ErrorCode.INSUFFICIENT_BALANCE_TRANSACTION);
        }
        account.updateBalance(account.getBalance().subtract(amount));
        accountRepository.update(account);
        log.info("출금 완료. 계좌 ID: {}, 현재 잔액: {}", account.getId(), account.getBalance());
    }

    // 거래 기록 저장 로직
    private void saveTransaction(Account account, TransactionType type, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .accountId(account.getId())
                .transactionType(type)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    public CreateAccountResDTO createAccount(AuthUser user, CreateAccountReqDTO dto) {
        // 1. 유저 확인.
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

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

    public FindAccountResDTO findByAccountNumber(AuthUser user, FindAccountReqDTO dto)  {
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 존재하는 계좌인지 확인.
        Account findAccount = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 계좌 비밀번호가 일치하는지 확인.
        validatePassword(findAccount, dto.getAccountPassword());

        return FindAccountResDTO.generate(findAccount);
    }

    private void isUserAccount(User user, Account account) {
        if (!user.getId().equals(account.getUserId())) {
            throw new ForbiddenException(ErrorCode.ACCOUNT_ACCESS_DENIED);
        }
    }

    private void validatePassword(Account account, String password)  {
        if (!account.getAccountPassword().equals(password)) {
            throw new UnauthorizedException(ErrorCode.INVALID_ACCOUNT_PASSWORD);
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
