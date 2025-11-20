package com.baas.securities.controller;

import com.baas.securities.dto.ResponseDTO;
import com.baas.securities.dto.account.*;
import com.baas.securities.dto.agreement.UserAgreementReqDTO;
import com.baas.securities.dto.agreement.UserAgreementResDTO;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.security.resolver.Login;
import com.baas.securities.service.AccountService;
import com.baas.securities.service.UserAgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
/**
 * 계좌 개설 흐름.
 * 1. 이메일 인증 (EmailController)
 * 2. 약관 동의 (/api/accounts/agreement)
 * 3. 계좌 개설 (/api/accounts : POST)
 */
public class AccountController {
    private final UserAgreementService agreementService;
    private final AccountService accountService;

    @PostMapping("/agreement")
    public ResponseDTO saveAgreement(@Login AuthUser user, @RequestBody UserAgreementReqDTO dto) throws IllegalAccessException {
        UserAgreementResDTO savedDto = agreementService.save(user, dto);
        log.info("user={}, agreement id={}", user.getEmail(), savedDto.getAgreementId());

        return new ResponseDTO(HttpStatus.CREATED, "사용자 동의 정보가 생성되었습니다.", savedDto);
    }

    @PatchMapping("/agreement")
    public ResponseDTO updateAgreement(@Login AuthUser user, @RequestBody UserAgreementReqDTO dto) {
        log.info("agreement={}", dto);
        UserAgreementResDTO updatedDto = agreementService.update(user, dto);
        log.info("user={} agreement_id={}", user.getEmail(), updatedDto.getAgreementId());

        return new ResponseDTO(HttpStatus.OK, "사용자 동의 정보가 업데이트 되었습니다.", updatedDto);
    }

    @PostMapping
    public ResponseDTO createAccount(@Login AuthUser user, @RequestBody CreateAccountReqDTO dto) {
        log.info("user={}, account type={}", user.getEmail(), dto.getAccountType());

        CreateAccountResDTO savedDto = accountService.createAccount(user, dto);
        log.info("CREATE account number={}", savedDto.getAccountNumber());

        return new ResponseDTO(HttpStatus.CREATED, "생성 완료", savedDto);
    }

    @GetMapping
    public ResponseDTO findAllAccount(@Login AuthUser user) {
        log.info("user={}", user.getEmail());

        FindAllAccountResDTO resDTO = accountService.findAllByUserEmail(user);
        log.info("user={}, accounts={}", user.getEmail(), resDTO.getAccounts().size());

        return new ResponseDTO(HttpStatus.OK, "조회 성공", resDTO);
    }

    /**
     * 3-4. 입금 / 출금
     */

    @PostMapping("/{accountId}/transaction")
    public ResponseDTO processTransaction(
            @Login AuthUser user,
            @PathVariable String accountId,
            @RequestBody TransactionReqDTO dto){
        log.info("TRANSACTION user={}, accountId={}, type={}, amount={}", user.getEmail(), accountId, dto.getTransactionType(), dto.getAmount());
        TransactionResDTO resDTO = accountService.processTransaction(user, accountId, dto);
        return new ResponseDTO(HttpStatus.OK,"처리 완료",resDTO);
    }

    @PostMapping("/transfer")
    public ResponseDTO transferFunds(@Login AuthUser user, @RequestBody TransferReqDTO dto) {
        log.info("TRANSFER user={}, fromAcc={}, toAcc={}, amount={}",
                user.getEmail(), dto.getFromAccountNumber(), dto.getToAccountNumber(), dto.getAmount());

        TransferResDTO resDTO = accountService.transfer(user, dto);
        return new ResponseDTO(HttpStatus.OK, "송금이 완료되었습니다.", resDTO);


    }
}
