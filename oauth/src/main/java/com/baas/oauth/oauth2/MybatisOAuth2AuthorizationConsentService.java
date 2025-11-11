package com.baas.oauth.oauth2;

import com.baas.oauth.model.Consent;
import com.baas.oauth.mapper.ConsentMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.Set;

@Component()
@RequiredArgsConstructor
public class MybatisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final ConsentMapper consentMapper;
    private final RegisteredClientRepository registeredClientRepository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {

        // 먼저 Mybatis DTO로 변환
        Consent consentDto = toDto(authorizationConsent);

        // 2. Mapper에 정의한 findById로 기존 데이터가 있는지 확인
        Consent existingConsent = consentMapper.findById(
                consentDto.getRegisteredClientId(),
                consentDto.getPrincipalName()
        );

        if (existingConsent != null) {
            // 3. 데이터가 있으면 update 호출
            consentMapper.update(consentDto);
        } else {
            // 4. 데이터가 없으면 save (insert) 호출
            consentMapper.save(consentDto);
        }
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        // 5. Mapper에 정의한 deleteById (복합키 파라미터) 호출
        consentMapper.deleteById(
                authorizationConsent.getRegisteredClientId(),
                authorizationConsent.getPrincipalName()
        );
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        // 6. Mapper에 정의한 findById 호출
        Consent consentDto = consentMapper.findById(registeredClientId, principalName);

        // 7. Mybatis는 Optional이 아닌 DTO를 직접 반환하므로, null 체크로 변경
        if (consentDto == null) {
            return null;
        }

        // 8. 헬퍼 메소드(toObject)로 변환하여 반환
        return toObject(consentDto);
    }

    /**
     * Mybatis DTO -> Spring Security 객체
     */
    private OAuth2AuthorizationConsent toObject(Consent authorizationConsent) {
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

    /**
     * Spring Security 객체 -> Mybatis DTO
     * (메소드명 toEntity -> toDto로 변경)
     */
    private Consent toDto(OAuth2AuthorizationConsent authorizationConsent) {
        Consent dto = new Consent();
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