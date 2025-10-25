package com.baas.bank.auth.mapper;

import com.baas.bank.oauth2.dto.AuthorizationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AuthorizationMapper {

    /**
     * 인가 정보를 저장하거나(ID가 없으면) 업데이트합니다(ID가 있으면).
     */
    void saveOrUpdate(AuthorizationDto dto);

    void deleteById(String id);

    AuthorizationDto findById(String id);

    Optional<AuthorizationDto> findByState(String state);

    Optional<AuthorizationDto> findByAuthorizationCodeValue(String token);

    Optional<AuthorizationDto> findByAccessTokenValue(String token);

    Optional<AuthorizationDto> findByRefreshTokenValue(String token);

    Optional<AuthorizationDto> findByOidcIdTokenValue(String token);

    Optional<AuthorizationDto> findByUserCodeValue(String token);

    Optional<AuthorizationDto> findByDeviceCodeValue(String token);

    Optional<AuthorizationDto> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(String token);
}