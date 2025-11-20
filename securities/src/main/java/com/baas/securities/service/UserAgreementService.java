package com.baas.securities.service;

import com.baas.securities.dto.agreement.UserAgreementReqDTO;
import com.baas.securities.dto.agreement.UserAgreementResDTO;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.ConflictException;
import com.baas.securities.exception.ex.NotFoundException;
import com.baas.securities.repository.UserAgreementRepository;
import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.User;
import com.baas.securities.repository.entity.UserAgreement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAgreementService {

    private final UserAgreementRepository agreementRepository;
    private final UserRepository userRepository;

    /**
     * user agreement db에 저장.
     */
    public UserAgreementResDTO save(AuthUser user, UserAgreementReqDTO dto) throws IllegalAccessException {

        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));


        exists(user);

        UserAgreement userAgreement = generateAgreement(findUser, dto);

        agreementRepository.save(userAgreement);
        return UserAgreementResDTO.generate(userAgreement);
    }

    /**
     * user agreement update
     * mapper에서 dto의 필드가 null이 아닐때 실행.
     */
    public UserAgreementResDTO update(AuthUser user, UserAgreementReqDTO dto) {
        UserAgreement userAgreement = agreementRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_AGREEMENT_NOT_FOUND));

        userAgreement.update(dto);

        agreementRepository.update(userAgreement);
        return UserAgreementResDTO.generate(userAgreement);
    }

    private UserAgreement generateAgreement(User user, UserAgreementReqDTO dto) {
        return UserAgreement.builder()
                .userId(user.getId())
                .termsOfService(dto.isTermsOfService())
                .privacyPolicy(dto.isPrivacyPolicy())
                .marketing(dto.isMarketing())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void exists(AuthUser user) throws IllegalAccessException {
        if (agreementRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException(ErrorCode.USER_AGREEMENT_ALREADY_EXISTS);
        }
    }
}
