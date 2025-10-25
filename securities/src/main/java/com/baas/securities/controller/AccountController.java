package com.baas.securities.controller;

import com.baas.securities.dto.ResponseDTO;
import com.baas.securities.dto.agreement.UserAgreementReqDTO;
import com.baas.securities.dto.agreement.UserAgreementResDTO;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.security.resolver.Login;
import com.baas.securities.service.UserAgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final UserAgreementService agreementService;

    @PostMapping("/agreement")
    public ResponseDTO saveAgreement(@Login AuthUser user, @RequestBody UserAgreementReqDTO dto) throws IllegalAccessException {
        UserAgreementResDTO savedDto = agreementService.save(user, dto);

        return new ResponseDTO(HttpStatus.CREATED, "사용자 동의 정보가 생성되었습니다.", savedDto);
    }

    @PatchMapping("/agreement")
    public ResponseDTO updateAgreement(@Login AuthUser user, @RequestBody UserAgreementReqDTO dto) {
        log.info("agreement={}", dto);
        UserAgreementResDTO updatedDto = agreementService.update(user, dto);
        log.info("user={} agreement_id={}", user.getEmail(), updatedDto.getAgreementId());

        return new ResponseDTO(HttpStatus.OK, "사용자 동의 정보가 업데이트 되었습니다.", updatedDto);
    }
}
