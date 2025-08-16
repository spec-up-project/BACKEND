package com.neekly_report.whirlwind.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Builder
public class CategoryDto {

    public static class Request {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CategoryCreateRequest {
            private String parentUid;
            @NotBlank(message = "Category name is required")
            private String name;
            private String depth;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CategoryUpdateRequest {

            private String parentUid;
            private String categoryUid;
            @NotBlank(message = "Category name is required")
            private String name;
            private String depth;
        }
    }

    public static class Response {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CategoryResponse {

            private String parentUid;
            private String categoryUid;
            private String name;
            private String depth;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
        }
    }
}
