package com.baas.securities.controller;

import com.baas.securities.dto.JwtDto;
import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.User;
import com.baas.securities.security.util.JwtHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JwtGetterController {

    private final UserRepository userRepository;
    private final JwtHandler jwtHandler;

    @PostConstruct
    private void init() {
        String userEmail1 = "user1@gmail.com";
        User user1 = userRepository.findByEmail(userEmail1).orElseGet(() -> {
            userRepository.save(genCustUser("user1", userEmail1, "01011111111"));

            return userRepository.findByEmail(userEmail1).get();
        });

        String userEmail2 = "user2@gmail.com";
        User user2 = userRepository.findByEmail(userEmail2).orElseGet(() -> {
            userRepository.save(genCustUser("user2", userEmail2, "01022222222"));

            return userRepository.findByEmail(userEmail2).get();
        });

        JwtDto user1Jwt = jwtHandler.generate(user1.getEmail());
        JwtDto user2Jwt = jwtHandler.generate(user2.getEmail());

        log.info("user1 jwts={}", user1Jwt);
        log.info("user2 jwts={}", user2Jwt);
    }

    private User genCustUser(String userName, String email, String phoneNumber) {
        return User.builder()
                .email(email)
                .name(userName)
                .role("ROLE_USER")
                .oauthId("CUSTOM")
                .oauthProvider("CUSTOM")
                .phoneNumber(phoneNumber)
                .birthDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .build();
    }
}
