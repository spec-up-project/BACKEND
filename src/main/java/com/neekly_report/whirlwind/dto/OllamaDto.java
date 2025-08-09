package com.neekly_report.whirlwind.dto;

import lombok.*;

public class OllamaDto {
    // 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaRequest {
        private String model;
        private String prompt;
        private boolean stream;
    }

    // 응답 DTO (필요한 필드만 선언)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaResponse {
        private String response;
    }
}
