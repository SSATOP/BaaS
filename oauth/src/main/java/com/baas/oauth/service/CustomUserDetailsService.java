package com.baas.oauth.service;

import com.baas.oauth.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.baas.oauth.model.User userDto = userMapper.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(userDto.getUserId())
                .password(userDto.getPassword())
                .roles(userDto.getUserRole())
                .build();
    }
}
