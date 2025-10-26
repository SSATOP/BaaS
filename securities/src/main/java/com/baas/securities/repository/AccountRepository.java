package com.baas.securities.repository;

import com.baas.securities.repository.entity.Account;

import java.util.List;
import java.util.Optional;


public interface AccountRepository {
    Optional<Account> findById(String id);

    void save(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByEmail(String email);
    void update(Account account);
}
