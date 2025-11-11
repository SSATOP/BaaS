package com.baas.oauth.config;

import com.baas.oauth.mapper.UserMapper;
import com.baas.oauth.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserMapper userMapper;

    public SecurityConfig(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServer(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer()
                        .oidc(oidc -> oidc
                                .userInfoEndpoint(userInfo -> userInfo.userInfoMapper(this::buildUserInfo)));
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

        return http.build();
    }

    private OidcUserInfo buildUserInfo(OidcUserInfoAuthenticationContext context) {
        String username = context.getAuthorization().getPrincipalName();
        User user = userMapper.findByUserId(username).orElse(null);

        OidcUserInfo.Builder builder = OidcUserInfo.builder()
                .subject(username);

        if (user != null) {
            if (user.getUsername() != null) {
                builder.claim("name", user.getUsername());
            }
            if (user.getEmail() != null) {
                builder.email(user.getEmail());
            }
            if (user.getGender() != null) {
                builder.claim("gender", user.getGender());
            }
            if (user.getPhone() != null) {
                builder.claim("phone_number", user.getPhone());
            }
        }
        return builder.build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            String username = context.getPrincipal().getName();
            User user = userMapper.findByUserId(username).orElse(null);
            if (user == null) {
                return;
            }
            var requestedScopes = context.getAuthorizedScopes();
            String tokenType = context.getTokenType().getValue();
            
            // ID Token과 Access Token 모두에 사용자 정보 추가
            if ("id_token".equals(tokenType) || "access_token".equals(tokenType)) {
                // profile 범위에 따라 사용자 정보 추가
                if (requestedScopes.contains("profile")) {
                    if (user.getUsername() != null) {
                        context.getClaims().claim("name", user.getUsername());
                    }
                    if (user.getGender() != null) {
                        context.getClaims().claim("gender", user.getGender());
                    }
                }
                // email 범위에 따라 이메일 추가
                if (requestedScopes.contains("email") && user.getEmail() != null) {
                    context.getClaims().claim("email", user.getEmail());
                    if ("id_token".equals(tokenType)) {
                        context.getClaims().claim("email_verified", false);
                    }
                }
                // phone 범위에 따라 전화번호 추가
                if (requestedScopes.contains("phone") && user.getPhone() != null) {
                    context.getClaims().claim("phone_number", user.getPhone());
                    if ("id_token".equals(tokenType)) {
                        context.getClaims().claim("phone_number_verified", false);
                    }
                }
            }
        };
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf) -> csrf.disable());
        http
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().permitAll());
        http
                .formLogin(withDefaults());

        return http.build();
    }

    //비대칭 키 발급부
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return keyPair;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {

        return AuthorizationServerSettings.builder()
                .build();
    }
}
