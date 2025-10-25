package com.baas.securities.repository;

import com.baas.securities.repository.entity.EmailValidation;

import java.util.Optional;

public interface EmailValidationRepository {
    Optional<EmailValidation> findById(String id);

    Optional<EmailValidation> findByEmail(String email);

    int save(EmailValidation emailCheck);

    int update(EmailValidation emailCheck);
}
