package com.neekly_report.whirlwind.ocr;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class OcrDTO {

    public static class Request {

        @Getter
        @Setter
        public static class ImageUploadRequest {
            private MultipartFile image;
        }
    }

    public static class Response {

        @Getter
        @Builder
        public static class OcrResultResponse {
            private String extractedText;
            private String summarizedText;
        }
    }
}
