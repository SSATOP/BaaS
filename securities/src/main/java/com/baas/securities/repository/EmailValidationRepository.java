package com.baas.securities.repository;

import com.baas.securities.repository.entity.EmailValidation;

import java.util.Optional;

public interface EmailValidationRepository {
    Optional<EmailValidation> findById(String id);

    Optional<EmailValidation> findFirstByEmail(String email);

    void save(EmailValidation validation);

    void updateIsAndAtVerified(EmailValidation validation);
}
