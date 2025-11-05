package com.baas.securities.repository.dao;

import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDaoTest {

    @Autowired
    private UserRepository userRepository;
    @Test
    @DisplayName("유저 생성 테스트")
    void userSaveTest() {
        // given
        User user = User.builder()
                .email("user2@email.com")
                .name("user2")
                .role("ROLE_USER")
                .oauthId("CUSTOM")
                .oauthProvider("CUSTOM")
                .phoneNumber("01035661123")
                .birthDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .build();

//        userDao.save(user);


        // when
        Optional<User> findUser = userRepository.findByEmail(user.getEmail());

        System.out.println(user.getId());
        // then
        Assertions.assertThat(findUser.get()).isNotNull();
        Assertions.assertThat(findUser.get().getEmail()).isEqualTo("user2@email.com");
    }
}