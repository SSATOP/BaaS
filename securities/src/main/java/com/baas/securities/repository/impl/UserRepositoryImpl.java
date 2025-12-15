package com.baas.securities.repository;

import com.baas.securities.dto.UserDto;
import com.baas.securities.repository.dao.UserDao;
import com.baas.securities.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserDao userDao;

    @Override
    public Optional<User> findByEmail(String email) {
        UserDto userDto = userDao.findByEmail(email);

        if (userDto == null) {
            return Optional.empty();
        }

        // DTO -> Entity 변환
        User user = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .role(userDto.getRole())
                .oauthProvider(userDto.getOauthProvider())
                .oauthId(userDto.getOauthId())
                .build();

        // 중요: DB에 있는 UUID를 Entity에 주입
        // (User 클래스의 setter가 있다면 사용, 없다면 Builder에 id 필드 추가 필요)
        user.setId(userDto.getId());

        return Optional.of(user);
    }

    @Override
    public void save(User user) {
        // 1. DTO 변환 (User 엔티티의 모든 값을 DTO로 전달)
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .oauthProvider(user.getOauthProvider())
                .oauthId(user.getOauthId()) // *주의: CustomOAuth2UserService에서 이 값을 잃지 않도록 보장 필요*
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();

        // 2. DB에서 기존 DTO 정보 조회
        UserDto existingUserDto = userDao.findByEmail(userDto.getEmail());

        if (existingUserDto != null) {
            // 기존 회원: UPDATE 실행

            // DTO에 DB의 기존 필수 값(oauthId, createdAt 등)을 안전하게 채워 넣기
            // Client Service에서 변경하지 않은 필드는 DB 원본 값을 사용하도록 보장
            if (userDto.getOauthId() == null) {
                userDto.setOauthId(existingUserDto.getOauthId());
            }
            if (userDto.getPhoneNumber() == null) {
                userDto.setPhoneNumber(existingUserDto.getPhoneNumber());
            }
            if (userDto.getCreatedAt() == null) {
                userDto.setCreatedAt(existingUserDto.getCreatedAt());
            }
            if (userDto.getLastLogin() == null) {
                userDto.setLastLogin(existingUserDto.getLastLogin());
            }


            // ID를 DB 원본 ID로 설정
            userDto.setId(existingUserDto.getId());

            userDao.update(userDto);

        } else {
            // 신규 회원: INSERT 호출
            userDao.save(userDto);
        }
    }
    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }
}