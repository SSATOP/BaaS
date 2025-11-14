package com.baas.bank.auth.oauth2;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserDAO userDAO;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();

        log.info("registrationId:{}", registrationId);

        String providerId = "";
        String email = "";
        String name = "";
        String profileImgUrl = "";
        UserDto user = new UserDto();
//        {
//            "response": {
//            "id": "ssafy",
//                    "username": "test123",
//                    "gender": "남자",
//                    "email": "asdff@das.ager",
//                    "phoneNumber": "010-1234-1234"
//        }
//        }
        if (registrationId.equals("baas")) {
            Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
            providerId = "baas_" + responseMap.get("id");

        }
        return null;
//        if (registrationId.equals("naver")) {
//            Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
//            providerId = "naver_" + responseMap.get("id").substring(0, 14);
//            email = responseMap.get("email");
//            name = responseMap.get("name");
//            profileImgUrl = responseMap.get("profile_image");
//
//            String birthyear = responseMap.get("birthyear");
//            String birthday = responseMap.get("birthday");
//            if (birthyear != null && birthday != null) {
//                String fullDateStr = birthyear + "-" + birthday;
//                LocalDate localDate = LocalDate.parse(fullDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//                user.setBirthday(Timestamp.valueOf(localDate.atStartOfDay()));
//            }
//
//        } else if (registrationId.equals("google")) {
//            Map<String, Object> responseMap = oAuth2User.getAttributes();
//            providerId = "google_" + responseMap.get("sub").toString().substring(0, 14);
//            email = responseMap.get("email").toString();
//            name = responseMap.get("name").toString();
//            profileImgUrl = responseMap.get("picture").toString();
//
//
//        } else {
//            throw new JwtAuthException(AuthErrorCode.UNSUPPORTED_TYPE);
//        }
//
//        Optional<User> existData = userDAO.findByProviderId(providerId);
//        if (existData.isEmpty()) {
//            user.setEmail(email);
//            user.setName(name);
//            user.setProviderId(providerId);
//            user.setProfileImgUrl(profileImgUrl);
//
//            // 일반 회원가입을 진행한 이메일이 존재하는지
//            boolean existsByEmail = userDAO.existsByEmail(email);
//            if (existsByEmail) {
//                // 페이지 분할 방식이라면, 에러 호출 후 프론트에서 요청하는 방식으로
//                userDAO.updateSocialByEmail(user);  // 존재하면 연동
//            } else {
//                userDAO.saveSocial(user);   // 없으면 새로 생성
//            }
//
//            Long id = userDAO.findIdByEmail(user.getEmail());
//
//            OAuthUserDTO userDto = new OAuthUserDTO();
//            userDto.setId(id);
//            userDto.setProviderId(providerId);
//
//            return new CustomOAuth2User(userDto);
//
//        } else {    // 소셜 계정 존재
//            // 변경될 수 있는 값들 변경해주는 부분
//            // 매번 업데이트 해줄지, 아니면 초기 값만 받고 우리가 따로 관리할 지
////            user = existData.get();
//
////            user.setEmail(email);
////            user.setName(name);
////
////            userDAO.save(user);
//            Long id = existData.get().getId();
//
//            OAuthUserDTO userDto = new OAuthUserDTO();
//            userDto.setId(id);
//            userDto.setProviderId(providerId);
//
//            return new CustomOAuth2User(userDto);
//        }
    }
}
