package com.baas.securities.repository.impl;

import com.baas.securities.repository.AccountRepository;
import com.baas.securities.repository.entity.Account;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class AccountRepositoryImpl implements AccountRepository {

    private final Map<String,Account> store = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Account account = Account.createDummyAccount();
        save(account);
        log.info("saved account={}", account);
    }

    @Override
    public Optional<Account> findById(String id) {
        Account account = store.get(id);
        return Optional.of(account);
    }

    @Override
    public void save(Account account) {
        store.put(account.getId(),account);
    }
}
