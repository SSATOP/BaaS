package com.baas.securities.repository.impl;

import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(String id) {
        User user = store.get(id);
        return Optional.of(user);
    }

    @Override
    public void save(User user) {
        store.put(user.getId(), user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }


}
