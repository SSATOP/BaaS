package com.baas.securities.repository;

import com.baas.securities.repository.entity.EmailValidation;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailValidationRepository {
    Optional<EmailValidation> findById(String id);

    Optional<EmailValidation> findFirstByEmail(String email);

    EmailValidation save(EmailValidation validation);

    void updateIsAndAtVerified(EmailValidation validation);
}
