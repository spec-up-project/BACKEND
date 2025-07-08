package com.neekly_report.whirlwind.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // swaggerAPI 인증&인가 예외처리
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/v2/api-docs")
                .securityMatcher("/v3/api-docs")
                .securityMatcher("/v3/api-docs/**")
                .securityMatcher("/swagger-resources/**")
                .securityMatcher("/swagger-ui.html/**")
                .securityMatcher("/swagger-ui/**")
                .securityMatcher("/swagger/**")
                .securityMatcher("/webjars/**")
                .authorizeHttpRequests( auth -> auth.anyRequest().permitAll());

        return httpSecurity.build();
    }

}
