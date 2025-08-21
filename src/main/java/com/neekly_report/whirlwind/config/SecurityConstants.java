package com.neekly_report.whirlwind.config;

public class SecurityConstants {
    public static final String[] PERMIT_ALL_PATTERNS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/user/login",
            "/api/user/register",
            "/api/user/reissue"
    };
}

