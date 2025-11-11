package com.baas.oauth.service;

import com.baas.oauth.dto.RegisterDTO;
import com.baas.oauth.mapper.RegisterMapper;
import com.baas.oauth.model.Register;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final RegisterMapper registerMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 클라이언트를 등록하고, Mybatis DTO 객체를 반환합니다.
     * @param dto
     * @return Register (Mybatis DTO)
     */
    public Register register(RegisterDTO dto) {
        Register registerDto = new Register();

        // --- 객체에 값 설정 ---
        registerDto.setId(UUID.randomUUID().toString());
        registerDto.setClientId(UUID.randomUUID().toString());
        registerDto.setClientIdIssuedAt(Instant.now());
        registerDto.setClientSecret(bCryptPasswordEncoder.encode(dto.getClientSecret()));
        registerDto.setClientAuthenticationMethods("client_secret_basic");
        registerDto.setAuthorizationGrantTypes("refresh_token,authorization_code");

        registerDto.setClientName(dto.getClientName());
        registerDto.setRedirectUris(dto.getRedirectUris());
        registerDto.setPostLogoutRedirectUris(dto.getPostLogoutRedirectUris());
        registerDto.setScopes(dto.getScopes());

        // --- ClientSettings, TokenSettings JSON 문자열 설정 ---
        registerDto.setClientSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}");
        registerDto.setTokenSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.x509-certificate-bound-access-tokens\":false,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}");

        registerMapper.save(registerDto);

        return registerDto;
    }
}