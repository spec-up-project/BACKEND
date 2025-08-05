package com.neekly_report.whirlwind.todo;

import lombok.*;

import java.time.LocalDateTime;

@Builder
public class TodoDTO {

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
    }
}
