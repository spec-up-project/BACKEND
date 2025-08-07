package com.neekly_report.whirlwind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

public class CalendarDto {

    public static class Response {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
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
