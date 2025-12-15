package com.baas.securities.config;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.baas.securities.security.filter.JwtFilter;
import com.baas.securities.service.CustomOAuth2UserService;
import com.baas.securities.ws.OAuth2LoginSuccessHandler;
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

    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final  OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 1. 비활성화 설정 (충돌 해결 완료)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2. 권한 설정 통합 및 세션 관리 (한 번만 호출해야 함)
                .authorizeHttpRequests(auth -> auth
                        // OAuth 경로 (나의 브랜치)
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/error", "/favicon.ico").permitAll()

                        // API 및 웹소켓 경로 (원본 브랜치)
                        .requestMatchers("/api/auth").permitAll()
                        .requestMatchers("/ws-stomp/**", "/ws-stomp-stock-order/**").permitAll()
                        .requestMatchers("/stock/period/**").permitAll()

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 3. 필터 및 OAuth2 설정 (체인 연결)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // 💡 세미콜론 제거
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/baas")
                        .authorizationEndpoint(authorization -> authorization

                                 .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // customOAuth2UserService 주입 필요
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // oAuth2LoginSuccessHandler 주입 필요
                );

        return http.build();
    }
}
