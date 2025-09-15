package com.project.sleep.global.config;

import com.project.sleep.global.resolver.CurrentUserArgumentResolver;
import com.project.sleep.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(List.of(
                new CurrentUserArgumentResolver(tokenProvider)
        ));
    }
}
