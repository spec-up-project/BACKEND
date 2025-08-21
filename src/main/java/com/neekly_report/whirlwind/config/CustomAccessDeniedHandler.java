package com.neekly_report.whirlwind.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        String path = request.getRequestURI();

        for (String pattern : SecurityConstants.PERMIT_ALL_PATTERNS) {
            if (pathMatcher.match(pattern, path)) {
                return;
            }
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "ACCESS_DENIED");
        errorResponse.put("message", "접근 권한이 없습니다.");

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}



