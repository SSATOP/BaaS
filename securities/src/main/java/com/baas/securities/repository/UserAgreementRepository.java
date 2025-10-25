package com.baas.securities.repository;

import com.baas.securities.repository.entity.UserAgreement;

import java.util.Optional;

public interface UserAgreementRepository {
    Optional<UserAgreement> findById(String id);

    Optional<UserAgreement> findByEmail(String userId);

    UserAgreement save(UserAgreement userAgreement);

    void update(UserAgreement userAgreement);
}
