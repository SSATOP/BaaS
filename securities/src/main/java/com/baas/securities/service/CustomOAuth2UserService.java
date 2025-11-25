package com.baas.securities.service;

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
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if(response == null){
            response = attributes;
        }

        //  뱅킹팀이 보내주는 JSON 키값에 맞춰 수정해야 함
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        log.info("뱅킹 로그인 시도: email={}, name={}", email, name);

//        // 3. DB 저장/업데이트 로직 (UserDao 활용)
//        // 예시 코드 (집에 가서 DB 연결되면 주석 풀고 완성하세요)
//
//        UserDto user = userDao.findByEmail(email);
//        if (user == null) {
//            // (A) DB에 없으면? -> 신규 회원가입 (INSERT)
//            log.info("신규 회원입니다. 자동 회원가입 진행: {}", email);
//            UserDto newUser = new UserDto();
//            newUser.setEmail(email);
//            newUser.setName(name);
//            newUser.setRole("ROLE_USER");
//            newUser.setOauthProvider("BAAS"); // 뱅킹에서 왔다는 표시
//            userDao.save(newUser); // <-- 여기가 회원가입!
//        } else {
//            // (B) DB에 있으면? -> 그냥 통과 (로그인)
//            log.info("기존 회원입니다. 로그인 처리: {}", email);
//            // 필요하면 여기서 최근 접속일(last_login) 업데이트
//        }

    }
}
