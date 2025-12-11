package com.baas.securities.repository.dao;

import com.baas.securities.dto.UserDto;
import com.baas.securities.repository.UserRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper
public interface UserDao  {
    // 이메일로 회원 조회
    UserDto findByEmail(String email);

    // 회원 정보 저장 (회원가입)
    void save(UserDto userDto);

    void update(UserDto userDto);
}
