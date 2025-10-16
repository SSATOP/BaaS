package com.baas.bank.auth.repository;

import com.baas.bank.auth.entity.EmailVerify;
import org.springframework.data.repository.CrudRepository;

public interface EmailVerifyRepository extends CrudRepository<EmailVerify, String> {
}
