package com.neekly_report.whirlwind.dto;

import com.neekly_report.whirlwind.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public class ScheduleDto {

    public static class Request {

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleCreateRequest {
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source; // "TEXT", "FILE"
            private User user;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleUpdateRequest {
            private String tScheduleUid;
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source; // "TEXT", "FILE"
            private User user;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class TextBasedScheduleRequest {
            @NotBlank(message = "텍스트는 필수 입력값입니다.")
            private String text;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ModifyExtractedScheduleRequest {
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source;
        }
    }

    public static class Response {

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleResponse {
            private String tScheduleUid;
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ExtractedSchedulePreview {
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
        }
    }
}

