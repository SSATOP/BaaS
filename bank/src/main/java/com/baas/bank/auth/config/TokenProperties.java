package com.baas.bank.auth.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class TokenProperties {
    private TokenConfig accessToken;
    private TokenConfig refreshToken;

    @Setter
    @Getter
    public static class TokenConfig {
        private String name;
        private long expiry;
    }
}