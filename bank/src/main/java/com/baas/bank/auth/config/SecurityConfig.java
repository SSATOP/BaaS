package com.baas.bank.auth.config;

import com.baas.bank.auth.dao.RefreshDAO;
import com.baas.bank.auth.dao.UserDAO;
import com.baas.bank.auth.filter.JwtAuthenticationFilter;
import com.baas.bank.auth.filter.JwtLoginFilter;
import com.baas.bank.auth.filter.JwtLogoutFilter;
import com.baas.bank.auth.provider.JwtProvider;
import com.baas.bank.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final TokenProperties tokenProperties;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserDAO userDAO;
    private final RefreshDAO refreshDAO;
    private final JwtService jwtService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws  Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((sm)->{
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(customAuthenticationEntryPoint)       // 인증 실패 시
//                        .accessDeniedHandler(customAccessDeniedHandler)                 // 인가 실패 시
//                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(//back-end
                                "/swagger-ui/**", "/auth/email/**","/auth/reissue","/users/**","/login"
                        ).permitAll()
                        .requestMatchers(//front-end
                                "/templates/**","/static/**","signup.html"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProperties, jwtProvider, userDAO), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new JwtLoginFilter(tokenProperties, authenticationManager(authenticationConfiguration), jwtProvider, jwtService, refreshDAO, userDAO), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtLogoutFilter(tokenProperties, jwtProvider, refreshDAO), LogoutFilter.class)
        ;
        return http.build();

    }

}
