package com.baas.securities.config;

import com.baas.securities.security.resolver.LoginAnnotationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    private final LoginAnnotationResolver loginAnnotationResolver;

    /**
     * cors 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Set-Cookie")
                .allowedMethods("**")
                .allowedHeaders("*")
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAnnotationResolver);
    }
}
