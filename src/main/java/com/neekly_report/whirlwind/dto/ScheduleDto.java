package com.neekly_report.whirlwind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neekly_report.whirlwind.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public class ScheduleDto {

    public static class Request {

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleCreateRequest {
            private String rawText;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleUpdateRequest {
            private String scheduleUid;
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
            private String scheduleUid;
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private Boolean isAllDay;
            private String mainCategory;
            private String subCategory;
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

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString(exclude = {"scheduleId", "rawText"})
        public static class CalendarEvent {
            private String scheduleId;
            private String title;
            private String content;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime startTime;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime endTime;

            private Boolean isAllDay;
            private String source;
            private String rawText;

            private String mainCategory;
            private String subCategory;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime createDate;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime modifyDate;

            // 편의 메서드
            public long getDurationMinutes() {
                if (startTime != null && endTime != null) {
                    return java.time.Duration.between(startTime, endTime).toMinutes();
                }
                return 0;
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CalendarEventPreview {
            private String title;
            private String content;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime startTime;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime endTime;

            private String rawText;
        }
    }
}

