package com.baas.bank.user.mapper;

import com.baas.bank.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(UserDto user);

    UserDto findByLoginId(String loginId);

    UserDto findByLoginIdWithoutPassword(String loginId);

    UserDto findByEmail(String email);

    UserDto findById(Long userId);
}
