package com.baas.bank.account.service;

import com.baas.bank.account.dto.UserDto;
import com.baas.bank.account.exception.InvalidCredentialsException;
import com.baas.bank.account.exception.InvalidPasswordException;
import com.baas.bank.account.exception.UserAlreadyExistsException;
import com.baas.bank.account.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final  UserMapper userMapper;

    public void registUser(UserDto userDto){
        // 아이디 중복 체크
        if(userMapper.findByLoginIdWithoutPassword(userDto.getLoginId()) != null) {
            throw new UserAlreadyExistsException("이미 존재하는 사용자입니다.");
        }
        
        // 이메일 중복 체크
        if(userMapper.findByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistsException("이미 존재하는 이메일입니다.");
        }
        
        // 비밀번호 규칙 체크 (예: 8자 이상, 특수문자 포함 등)
        if(!isValidPassword(userDto.getLoginPassword())) {
            throw new InvalidPasswordException("비밀번호는 8자 이상이고 특수문자를 포함해야 합니다.");
        }
        
        userMapper.insertUser(userDto);
    }
    
    private boolean isValidPassword(String password) {
        // 비밀번호 규칙: 8자 이상, 특수문자 포함
        if(password == null || password.length() < 8) {
            return false;
        }
        
        // 특수문자 포함 체크
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        if(!hasSpecialChar) {
            return false;
        }
        
        return true;
    }
    public UserDto findByLoginId(String loginId){
        return userMapper.findByLoginId(loginId);
    }

    public UserDto findByLoginIdWithoutPassword(String loginId){
        return userMapper.findByLoginIdWithoutPassword(loginId);
    }

    public boolean isLoginIdDuplicate(String loginId) {
        UserDto existingUser = userMapper.findByLoginIdWithoutPassword(loginId);
        return (existingUser != null);
    }

    public boolean isEmailDuplicate(String email) {
        UserDto existingUser = userMapper.findByEmail(email);
        return (existingUser != null);
    }
}
