package com.baas.securities.config;


import com.baas.securities.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // formLogin 비활성화
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // CSRF 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable);

        // session 비활성화
        http
                .sessionManagement(session -> {
                    session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        http
                .authorizeHttpRequests((auth) -> { auth
                        .requestMatchers("/api/auth").permitAll()
                        // websocket 연결 허용
                        // 주식 주문 websocket 연결 허용
                        .requestMatchers("/ws-stomp/**", "/ws-stomp-stock-order/**").permitAll()
                        .requestMatchers("/stock/period/**").permitAll()
                        .anyRequest().authenticated();
                });

        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
