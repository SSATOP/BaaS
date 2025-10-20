package com.baas.securities.repository;



import com.baas.securities.repository.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(String id);
    void save(User user);
}
