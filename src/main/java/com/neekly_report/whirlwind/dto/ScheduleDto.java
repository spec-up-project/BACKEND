package com.neekly_report.whirlwind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class ScheduleDto {

    public static class Request {

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleAutoCreateRequest {
            private String rawText;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ScheduleManualCreateRequest {
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private Boolean isAllDay;

            private String categoryUid;
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
            private Boolean isAllDay;

            private String categoryUid;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class ModifyExtractedScheduleRequest {
            private String title;
            private String content;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source;
            private Boolean isAllDay;
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
            private String source;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;

            private String categoryUid;
            private List<CategoryDto.Response.CategoryResponse> category;
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
        @ToString(exclude = {"scheduleUid", "rawText"})
        public static class CalendarEvent {
            private String scheduleUid;
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
        public static class ScheduleEvent {
            private String title;
            private String content;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime startTime;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime endTime;

            private String rawText;
            private Boolean isAllDay;
        }
    }
}

