package com.baas.oauth.service;

import com.baas.oauth.dto.UserDTO;
import com.baas.oauth.mapper.UserMapper;
import com.baas.oauth.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public void join(UserDTO dto) {
        User user = User.builder()
                .userId(dto.getUserId())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .userRole("ADMIN")
                .username(dto.getUsername())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .build();

        log.info(user.toString());

        userMapper.save(user);
    }
}
