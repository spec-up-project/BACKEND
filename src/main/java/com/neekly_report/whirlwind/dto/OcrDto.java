package com.neekly_report.whirlwind.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class OcrDto {

    public static class Request {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ImageUploadRequest {
            private MultipartFile image;
            private String language = "kor+eng"; // 기본값 설정
        }
    }

    public static class Response {

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OcrResultResponse {
            private String extractedText;
            private String summarizedText;
            private Boolean success;
            private String errorMessage;
            private Long processingTimeMs;
            private Integer savedSchedulesCount;
            private Integer savedTodosCount;
        }
    }
}
