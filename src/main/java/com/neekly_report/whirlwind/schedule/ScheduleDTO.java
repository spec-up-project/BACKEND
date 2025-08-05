package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public class ScheduleDTO {

    public static class Request {
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ScheduleCreateRequest {
            private String title;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source; // "TEXT", "FILE" 등
            private User user;
        }
    }

    public static class Response {
        @Getter
        @Setter
        @NoArgsConstructor
        @Builder
        @AllArgsConstructor
        public static class ScheduleResponse {
            private String tScheduleUid;
            private String title;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            private String rawText;
            private String source;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
        }
    }
}
