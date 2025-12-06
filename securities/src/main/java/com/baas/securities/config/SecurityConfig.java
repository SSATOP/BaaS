package com.baas.securities.config;

import com.baas.securities.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService; // 뱅킹 서버에서 받아온 정보 처리기

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                        // 1. 로그인, 정적 리소스, 인증 없이 갈 수 있는 곳 허용
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/error", "/favicon.ico").permitAll()
                        // 2. 그 외에는 무조건 로그인 해야 함
                        .anyRequest().authenticated()
                );

        // ★ YML에 적은 설정을 여기서 활성화시킵니다!
        http
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/baas") // (선택) 로그인 시작 URL 명시
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // ★ 여기서 DB 저장 로직 연결!
                        )
                        .successHandler((request, response, authentication) -> {
                            // 로그인 성공 후 리다이렉트 할 경로 (예: 메인 페이지)
                            response.sendRedirect("/");
                            // 또는 프론트엔드 주소 (예: http://localhost:5173/success)
                        })
                );

        return http.build();
    }
}