package com.baas.securities.config;

import com.baas.securities.security.filter.JwtFilter;
import com.baas.securities.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. 기본 설정
        http
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 로그인 끄기
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 끄기
                .csrf(AbstractHttpConfigurer::disable);     // CSRF 끄기

        // 2. 세션 설정
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 3. URL 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        // [중요] OAuth2 로그인 관련 경로와 정적 리소스 허용
                        .requestMatchers("/login/**", "/oauth2/**", "/error", "/favicon.ico").permitAll()

                        // 인증 없이 접근 가능한 API (로그인, 회원가입 등)
                        .requestMatchers("/api/auth/**").permitAll()

                        // WebSocket 관련 경로 허용
                        .requestMatchers("/ws-stomp/**", "/ws-stomp-stock-order/**").permitAll()

                        // 테스트용 경로 (필요하다면)
                        .requestMatchers("/test/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        // 4. JWT 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 실행)
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 5. OAuth2 로그인 설정
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 뱅킹 서버에서 유저 정보 가져오는 서비스
                        )
                        .defaultSuccessUrl("/main", true) // 로그인 성공 시 이동할 곳 (true: 항상 이동)
                        .failureUrl("/login?error=true")  // 로그인 실패 시 이동할 곳
                );

        return http.build();
    }
}