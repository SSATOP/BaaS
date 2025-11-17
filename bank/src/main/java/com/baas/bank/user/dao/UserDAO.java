package com.baas.bank.user.dao;

import com.baas.bank.user.dto.UserDto;
import com.baas.bank.user.mapper.UserMapper;
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

    public Long findIdByEmail(String email) {
        return userMapper.findIdByEmail(email);
    }

    public Optional<UserDto> findByLoginId(String loginId) {
        UserDto user = userMapper.findByLoginId(loginId);
        return Optional.ofNullable(user);
    }


    public Optional<UserDto> findByProviderId(String provider, String providerId) {
        UserDto user = userMapper.findByProviderId(provider, providerId);
        return Optional.ofNullable(user);
    }

    public void saveSocial(UserDto user) {
        userMapper.saveSocial(user);
    }
}
