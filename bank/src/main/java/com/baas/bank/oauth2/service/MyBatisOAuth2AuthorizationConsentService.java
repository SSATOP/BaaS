package com.baas.bank.oauth2.service;

import com.baas.bank.oauth2.dto.ConsentDto;
import com.baas.bank.auth.mapper.ConsentMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class MyBatisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final ConsentMapper consentMapper; // Mapper 주입
    private final RegisteredClientRepository registeredClientRepository;

    public MyBatisOAuth2AuthorizationConsentService(ConsentMapper consentMapper, RegisteredClientRepository registeredClientRepository) { // 주입 변경
        this.consentMapper = consentMapper;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        consentMapper.saveOrUpdate(toDto(authorizationConsent)); // toEntity -> toDto
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        consentMapper.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return Optional.ofNullable(consentMapper.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName))
                .map(this::toObject)
                .orElse(null);
    }

    // toObject, toDto(구 toEntity) 메서드들은 Jpa...ConsentService와 100% 동일
    // (ConsentEntity -> ConsentDto 타입 변경만 적용)

    private OAuth2AuthorizationConsent toObject(ConsentDto authorizationConsent) { // Dto로 변경
        String registeredClientId = authorizationConsent.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                registeredClientId, authorizationConsent.getPrincipalName());
        if (authorizationConsent.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(authorizationConsent.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }

        return builder.build();
    }

    private ConsentDto toDto(OAuth2AuthorizationConsent authorizationConsent) { // Dto로 변경
        ConsentDto dto = new ConsentDto(); // Dto로 변경
        dto.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        dto.setPrincipalName(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        dto.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));

        return dto;
    }
}