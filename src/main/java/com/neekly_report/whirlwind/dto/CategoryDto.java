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
            @NotBlank(message = "Category name is required")
            private String categoryName;

            @NotBlank(message = "Segment type is required")
            private String segType;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CategoryUpdateRequest {
            @NotBlank(message = "Category UID is required")
            private String tCategoryUid;

            @NotBlank(message = "Category name is required")
            private String categoryName;

            @NotBlank(message = "Segment type is required")
            private String segType;
        }
    }

    public static class Response {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CategoryResponse {
            private String tCategoryUid;
            private String categoryName;
            private String segType;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
        }
    }
}
