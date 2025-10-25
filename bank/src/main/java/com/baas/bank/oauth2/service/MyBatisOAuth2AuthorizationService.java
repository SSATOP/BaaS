package com.baas.bank.oauth2.service;

import com.baas.bank.oauth2.dto.AuthorizationDto;
import com.baas.bank.auth.mapper.AuthorizationMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class MyBatisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final AuthorizationMapper authorizationMapper;
    private final RegisteredClientRepository registeredClientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MyBatisOAuth2AuthorizationService(AuthorizationMapper authorizationMapper, RegisteredClientRepository registeredClientRepository) { // 주입 변경
        this.authorizationMapper = authorizationMapper;
        this.registeredClientRepository = registeredClientRepository;

        ClassLoader classLoader = MyBatisOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        authorizationMapper.saveOrUpdate(toDto(authorization));
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        authorizationMapper.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return Optional.ofNullable(authorizationMapper.findById(id))
                .map(this::toObject)
                .orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Optional<AuthorizationDto> result; // Dto 타입으로 변경
        if (tokenType == null) {
            result = this.authorizationMapper.findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByAuthorizationCodeValue(token);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByAccessTokenValue(token);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByRefreshTokenValue(token);
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByOidcIdTokenValue(token);
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByUserCodeValue(token);
        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
            result = this.authorizationMapper.findByDeviceCodeValue(token);
        } else {
            result = Optional.empty();
        }

        return result.map(this::toObject).orElse(null);
    }


    private OAuth2Authorization toObject(AuthorizationDto dto) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(dto.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + dto.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(dto.getId())
                .principalName(dto.getPrincipalName())
                .authorizationGrantType(resolveAuthorizationGrantType(dto.getAuthorizationGrantType()))
                .authorizedScopes(StringUtils.commaDelimitedListToSet(dto.getAuthorizedScopes()))
                .attributes(attributes -> attributes.putAll(parseMap(dto.getAttributes())));


        if (dto.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, dto.getState());
        }

        if (dto.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    dto.getAuthorizationCodeValue(),
                    dto.getAuthorizationCodeIssuedAt(),
                    dto.getAuthorizationCodeExpiresAt());
            builder.token(authorizationCode, metadata -> metadata.putAll(parseMap(dto.getAuthorizationCodeMetadata())));
        }

        if (dto.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    dto.getAccessTokenValue(),
                    dto.getAccessTokenIssuedAt(),
                    dto.getAccessTokenExpiresAt(),
                    StringUtils.commaDelimitedListToSet(dto.getAccessTokenScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(dto.getAccessTokenMetadata())));
        }

        if (dto.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    dto.getRefreshTokenValue(),
                    dto.getRefreshTokenIssuedAt(),
                    dto.getRefreshTokenExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(dto.getRefreshTokenMetadata())));
        }

        if (dto.getOidcIdTokenValue() != null) {
            OidcIdToken idToken = new OidcIdToken(
                    dto.getOidcIdTokenValue(),
                    dto.getOidcIdTokenIssuedAt(),
                    dto.getOidcIdTokenExpiresAt(),
                    parseMap(dto.getOidcIdTokenClaims()));
            builder.token(idToken, metadata -> metadata.putAll(parseMap(dto.getOidcIdTokenMetadata())));
        }

        if (dto.getUserCodeValue() != null) {
            OAuth2UserCode userCode = new OAuth2UserCode(
                    dto.getUserCodeValue(),
                    dto.getUserCodeIssuedAt(),
                    dto.getUserCodeExpiresAt());
            builder.token(userCode, metadata -> metadata.putAll(parseMap(dto.getUserCodeMetadata())));
        }

        if (dto.getDeviceCodeValue() != null) {
            OAuth2DeviceCode deviceCode = new OAuth2DeviceCode(
                    dto.getDeviceCodeValue(),
                    dto.getDeviceCodeIssuedAt(),
                    dto.getDeviceCodeExpiresAt());
            builder.token(deviceCode, metadata -> metadata.putAll(parseMap(dto.getDeviceCodeMetadata())));
        }

        return builder.build();
    }

    private AuthorizationDto toDto(OAuth2Authorization authorization) {
        AuthorizationDto dto = new AuthorizationDto();
        dto.setId(authorization.getId());
        dto.setRegisteredClientId(authorization.getRegisteredClientId());
        dto.setPrincipalName(authorization.getPrincipalName());
        dto.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        dto.setAuthorizedScopes(StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ","));
        dto.setAttributes(writeMap(authorization.getAttributes()));
        dto.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        setTokenValues(
                authorizationCode,
                dto::setAuthorizationCodeValue,
                dto::setAuthorizationCodeIssuedAt,
                dto::setAuthorizationCodeExpiresAt,
                dto::setAuthorizationCodeMetadata
        );

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        setTokenValues(
                accessToken,
                dto::setAccessTokenValue,
                dto::setAccessTokenIssuedAt,
                dto::setAccessTokenExpiresAt,
                dto::setAccessTokenMetadata
        );
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            dto.setAccessTokenScopes(StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ","));
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        setTokenValues(
                refreshToken,
                dto::setRefreshTokenValue,
                dto::setRefreshTokenIssuedAt,
                dto::setRefreshTokenExpiresAt,
                dto::setRefreshTokenMetadata
        );

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
                authorization.getToken(OidcIdToken.class);
        setTokenValues(
                oidcIdToken,
                dto::setOidcIdTokenValue,
                dto::setOidcIdTokenIssuedAt,
                dto::setOidcIdTokenExpiresAt,
                dto::setOidcIdTokenMetadata
        );
        if (oidcIdToken != null) {
            dto.setOidcIdTokenClaims(writeMap(oidcIdToken.getClaims()));
        }

        OAuth2Authorization.Token<OAuth2UserCode> userCode =
                authorization.getToken(OAuth2UserCode.class);
        setTokenValues(
                userCode,
                dto::setUserCodeValue,
                dto::setUserCodeIssuedAt,
                dto::setUserCodeExpiresAt,
                dto::setUserCodeMetadata
        );

        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode =
                authorization.getToken(OAuth2DeviceCode.class);
        setTokenValues(
                deviceCode,
                dto::setDeviceCodeValue,
                dto::setDeviceCodeIssuedAt,
                dto::setDeviceCodeExpiresAt,
                dto::setDeviceCodeMetadata
        );

        return dto;
    }

    private void setTokenValues(
            OAuth2Authorization.Token<?> token,
            Consumer<String> tokenValueConsumer,
            Consumer<Instant> issuedAtConsumer,
            Consumer<Instant> expiresAtConsumer,
            Consumer<String> metadataConsumer) {
        if (token != null) {
            OAuth2Token oAuth2Token = token.getToken();
            tokenValueConsumer.accept(oAuth2Token.getTokenValue());
            issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
            expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
            metadataConsumer.accept(writeMap(token.getMetadata()));
        }
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> metadata) {
        try {
            return this.objectMapper.writeValueAsString(metadata);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.DEVICE_CODE;
        }
        return new AuthorizationGrantType(authorizationGrantType);
    }
}