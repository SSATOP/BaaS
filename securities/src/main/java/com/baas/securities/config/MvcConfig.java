package com.baas.securities.config;

import com.baas.securities.security.resolver.LoginAnnotationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    private final LoginAnnotationResolver loginAnnotationResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAnnotationResolver);
    }
}
