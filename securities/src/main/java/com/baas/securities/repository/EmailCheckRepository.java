package com.baas.securities.repository;

import com.baas.securities.repository.entity.EmailCheck;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface EmailCheckRepository {
    Optional<EmailCheck> findById(String id);

    Optional<EmailCheck> findByEmail(String email);

    int save(EmailCheck emailCheck);

    int update(EmailCheck emailCheck);
}
