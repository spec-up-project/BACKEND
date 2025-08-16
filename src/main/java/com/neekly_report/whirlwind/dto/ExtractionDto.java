package com.neekly_report.whirlwind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ExtractionDto {

    public static class Request {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TextExtractionRequest {
            @NotBlank(message = "텍스트는 필수입니다")
            @Size(max = 5000, message = "텍스트는 5000자를 초과할 수 없습니다")
            private String text;

            @Builder.Default
            private String sourceType = "TEXT";
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EmailExtractionRequest {
            @NotBlank(message = "제목은 필수입니다")
            private String subject;

            @NotBlank(message = "본문은 필수입니다")
            @Size(max = 10000, message = "본문은 10000자를 초과할 수 없습니다")
            private String body;

            private String sender;
            private String recipient;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime receivedAt;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ImageExtractionRequest {
            private MultipartFile image;

            @Builder.Default
            private String language = "kor+eng";
        }
    }

    public static class Response {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ExtractionResult {
            private List<CalendarDto.Response.CalendarEvent> schedules;
            private String originalText;
            private String processedText;
            private String sourceType;
            private Long processingTimeMs;
            private Boolean success;
            private String errorMessage;
            private Integer savedEventsCount;
            private Integer savedTodosCount;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ExtractionPreview {
            private List<CalendarDto.Response.CalendarEventPreview> events;
            private List<TodoDto.Response.TodoItemPreview> todos;
            private String originalText;
            private String processedText;
            private Long processingTimeMs;
        }
    }
}
