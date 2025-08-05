package com.neekly_report.whirlwind.ocr;

import com.neekly_report.whirlwind.user.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrApiController {

    private final OcrService ocrService;

    @Operation(summary = "이미지 업로드 → 텍스트 요약 및 일정/할일 자동 등록")
    @PostMapping("/upload")
    public ResponseEntity<OcrDTO.Response.OcrResultResponse> uploadImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {

        OcrDTO.Response.OcrResultResponse result =
                ocrService.processImage(image, userDetail.getTUserUid());

        return ResponseEntity.ok(result);
    }
}
