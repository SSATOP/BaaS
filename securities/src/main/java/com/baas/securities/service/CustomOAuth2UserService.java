package com.baas.securities.service;

import com.baas.securities.dto.UserDto;
import com.baas.securities.repository.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserDao userDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 뱅킹 서버에서 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 User Info: {}", oAuth2User.getAttributes());

        // 2. 뱅킹 서비스인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("banking".equals(registrationId)) {
            processBankingUser(oAuth2User);
        }

        return oAuth2User;
    }

    private void processBankingUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 뱅킹팀의 응답 구조에 따라 response를 꺼내는 방식이 다를 수 있으므로 확인 필요
        // (보통 네이버 등은 response 안에 담겨 오지만, 뱅킹팀 API 명세에 따라 attributes 바로 사용)
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            response = attributes;
        }

        String email = (String) response.get("email");
        String name = (String) response.get("name");

        // 필수 정보가 없으면 로그 남기고 중단 (NullPointerException 방지)
        if (email == null) {
            log.error("OAuth2 로그인 실패: 이메일 정보가 없습니다. attributes={}", attributes);
            return;
        }

        log.info("뱅킹 로그인 시도: email={}, name={}", email, name);

        // 3. DB 저장/업데이트 로직
        UserDto user = userDao.findByEmail(email);

        if (user == null) {
            // (A) DB에 없으면 -> 신규 회원가입 (INSERT)
            log.info("신규 회원 감지. 자동 회원가입 진행: {}", email);

            UserDto newUser = new UserDto();
            newUser.setEmail(email);
            newUser.setName(name != null ? name : "이름없음"); // 이름 없을 경우 대비
            newUser.setRole("ROLE_USER");
            newUser.setOauthProvider("BAAS"); // 제공자 명시

            // 필요하다면 비밀번호는 임의의 값이나 NULL로 처리 (OAuth 유저는 비번 불필요할 수 있음)
            // newUser.setPassword("");

            userDao.save(newUser);
            log.info("회원가입 완료");
        } else {
            // (B) DB에 있으면 -> (선택) 정보 업데이트 or 로그인 로그 기록
            log.info("기존 회원 로그인: {}", email);

            // 예: 이름이 바뀌었을 수도 있으니 업데이트 (필요시 주석 해제)
            // user.setName(name);
            // userDao.update(user);
        }
    }}