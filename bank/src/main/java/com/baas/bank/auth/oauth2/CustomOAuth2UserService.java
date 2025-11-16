package com.baas.bank.auth.oauth2;

import com.baas.bank.auth.exception.JwtAuthException;
import com.baas.bank.user.dao.UserDAO;
import com.baas.bank.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserDAO userDAO;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();

        String provider = "";
        String oauthId = "";
        String email = "";
        String username = "";
        String gender = "";
        String phoneNumber = "";
        UserDto user = new UserDto();
        if (registrationId.equals("baas")) {
            Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
            provider = registrationId;
            oauthId = responseMap.get("id");
            email = responseMap.get("email");
            username = responseMap.get("username");
            // TODO : gender 값 활용 안함
//            gender = responseMap.get("gender");
            phoneNumber = responseMap.get("phoneNumber");
            log.info("responseMap:{}", responseMap);

        } else {
            throw new JwtAuthException(JwtAuthException.UNKNOWN_ERROR);
        }

        Optional<UserDto> existData = userDAO.findByProviderId(provider, oauthId);
        if (existData.isEmpty()) {
            user.setEmail(email);
            user.setName(username);
            user.setOauthProvider(provider);
            user.setOauthId(oauthId);
            user.setPhone(phoneNumber);

            // TODO 일반 회원가입과 중복으로 계정을 만들어 줄지에 관하여
            // 일반 회원가입을 진행한 이메일이 존재하는지
//            boolean existsByEmail = userDAO.existsByEmail(email);
//            if (existsByEmail) {
//                // 페이지 분할 방식이라면, 에러 호출 후 프론트에서 요청하는 방식으로
//                userDAO.updateSocialByEmail(user);  // 존재하면 연동
//            } else {
                userDAO.saveSocial(user);   // 없으면 새로 생성
//            }

            Long id = userDAO.findIdByEmail(user.getEmail());

            OAuthUserDTO userDto = new OAuthUserDTO();
            userDto.setId(id);
            userDto.setUsername(username);

            return new CustomOAuth2User(userDto);

        } else {    // 소셜 계정 존재
            Long id = existData.get().getId();

            OAuthUserDTO userDto = new OAuthUserDTO();
            userDto.setId(id);
            userDto.setUsername(username);

            return new CustomOAuth2User(userDto);
        }
    }
}
