package com.baas.bank.oauth2.service;

import com.baas.bank.oauth2.entity.RegisterEntity;
import com.baas.bank.auth.mapper.RegisterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final RegisterMapper registerMapper;
    private final PasswordEncoder bCryptPasswordEncoder;

    // 반환 타입을 Dto로 변경
    public RegisterEntity register(RegisterEntity dto) {

        RegisterEntity registerDto = new RegisterEntity();

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

        registerDto.setClientSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}");
        registerDto.setTokenSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.x509-certificate-bound-access-tokens\":false,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS2_56\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}");

        registerMapper.saveOrUpdate(registerDto);
        return registerDto;
    }
}
