package com.baas.oauth.mapper;

import com.baas.oauth.model.Authorization; // 1번에서 만든 DTO 클래스
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AuthorizationMapper {

    /**
     * ID로 인증 정보 조회
     *
     * @param id
     * @return Authorization
     */
    Optional<Authorization> findById(String id);

    /**
     * 인증 정보 저장
     *
     * @param authorization
     */
    void save(Authorization authorization);

    /**
     * 인증 정보 수정
     *
     * @param authorization
     */
    void update(Authorization authorization);

    /**
     * ID로 인증 정보 삭제
     *
     * @param id
     */
    void deleteById(String id);

    // Repository에 정의된 커스텀 메소드들
    Optional<Authorization> findByState(String state);

    Optional<Authorization> findByAuthorizationCodeValue(String authorizationCode);

    Optional<Authorization> findByAccessTokenValue(String accessToken);

    Optional<Authorization> findByRefreshTokenValue(String refreshToken);

    Optional<Authorization> findByOidcIdTokenValue(String idToken);

    Optional<Authorization> findByUserCodeValue(String userCode);

    Optional<Authorization> findByDeviceCodeValue(String deviceCode);

    // @Query에 해당되는 메소드
    Optional<Authorization> findByAnyToken(@Param("token") String token);

}