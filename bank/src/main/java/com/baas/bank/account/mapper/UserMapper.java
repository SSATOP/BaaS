package com.baas.bank.account.mapper;

import com.baas.bank.account.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(UserDto user);
    UserDto findByLoginId(@Param("loginId") String loginId);
    UserDto findByLoginIdWithoutPassword(@Param("loginId") String loginId);
    UserDto findByEmail(@Param("email") String email);
}
