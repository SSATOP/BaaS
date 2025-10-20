package com.baas.bank.auth.dao;

import com.baas.bank.account.dto.UserDto;
import com.baas.bank.account.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final UserMapper userMapper;

    public Optional<UserDto> findById(Long userId) {
        UserDto user = userMapper.findById(userId);
        return Optional.ofNullable(user);
    }

    public Optional<UserDto> findByEmail(String email) {
        UserDto user = userMapper.findByEmail(email);
        return Optional.ofNullable(user);
    }

    public Optional<UserDto> findByLoginId(String loginId) {
        UserDto user = userMapper.findByLoginId(loginId);
        return Optional.ofNullable(user);
    }
}
