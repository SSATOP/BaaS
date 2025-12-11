package com.baas.securities.service;

import com.baas.securities.repository.UserRepository;
import com.baas.securities.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // 1. UserDao 대신 UserRepository(인터페이스) 사용
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 User Info: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("현재 요청된 RegistrationId: {}", registrationId);
        // 뱅킹 서비스 연동일 경우 처리
        if ("baas".equals(registrationId)) {
            processBankingUser(oAuth2User);
        }

        return oAuth2User;
    }

    private void processBankingUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 뱅킹 API 응답 구조에 맞게 파싱
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            response = attributes;
        }

        String email = (String) response.get("email");
        String name = (String) response.get("name");
        String oauthId = (String) response.get("id");
        String phoneNumber = (String) response.get("phoneNumber");
        if (email == null) {
            log.error("OAuth2 로그인 실패: 이메일 정보가 없습니다. attributes={}", attributes);
            return;
        }

        log.info("뱅킹 로그인 시도: email={}, name={}", email, name);

        // 2. UserRepository를 통해 조회 (반환 타입 Optional<User>)
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // (A) 신규 회원가입
            log.info("신규 회원 감지. 자동 회원가입 진행: {}", email);

            // User 엔티티 빌더 사용
            // id는 User 클래스 생성자에서 UUID로 자동 생성되므로 넣지 않음
            User newUser = User.builder()
                    .email(email)
                    .name(name != null ? name : "이름없음")
                    .role("ROLE_USER")
                    .oauthProvider("BAAS") // 또는 "BANKING" 등 식별자
                    .phoneNumber(phoneNumber)
                    .oauthId(oauthId)
                    .createdAt(LocalDateTime.now()) // 생성 시간
                    .lastLogin(LocalDateTime.now()) // 마지막 로그인 시간
                    .build();

            userRepository.save(newUser);
            log.info("회원가입 완료");

        } else {
            // (B) 기존 회원 로그인
            log.info("기존 회원 로그인: {}", email);

            User existingUser = userOptional.get();

            // 로그인 시간 업데이트 등 필요한 로직이 있다면 여기서 처리
            existingUser.setLastLogin(LocalDateTime.now());
            userRepository.save(existingUser);
        }
    }
}