package com.neekly_report.whirlwind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public class TodoDto {

    public static class Request {
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class TodoCreateRequest {
            private String title;
            private String description;
            private String priority; // HIGH, MEDIUM, LOW
            private String category; // TODAY, THIS_WEEK, LATER
            private String status;   // DONE, TODO
            private LocalDateTime dueDate;
            private String rawText;
            private String source;   // TEXT, EMAIL, FILE 등
        }
    }

    public static class Response {
        @Getter
        @Setter
        @NoArgsConstructor
        @Builder
        @AllArgsConstructor
        public static class TodoResponse {
            private String tTodoUid;
            private String title;
            private String description;
            private String priority;
            private String category;
            private String status;
            private LocalDateTime dueDate;
            private String rawText;
            private String source;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TodoItem {
            private String todoId;
            private String title;
            private String description;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime dueDate;

            private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
            private String priority; // LOW, MEDIUM, HIGH, URGENT
            private String category; // TODAY, THIS_WEEK, LATER
            private String source;
            private String rawText;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime createDate;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime modifyDate;

            // 편의 메서드
            public boolean isOverdue() {
                return dueDate != null && dueDate.isBefore(LocalDateTime.now())
                        && !"COMPLETED".equals(status) && !"CANCELLED".equals(status);
            }

            public long getDaysUntilDue() {
                if (dueDate == null) return Long.MAX_VALUE;
                return java.time.Duration.between(LocalDateTime.now(), dueDate).toDays();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TodoItemPreview {
            private String title;
            private String description;

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime dueDate;

            private String priority;
            private String rawText;
        }
    }
}
