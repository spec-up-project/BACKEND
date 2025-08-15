package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.service.OllamaService;
import com.neekly_report.whirlwind.dto.ApiResponseDto;
import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.ExtractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "추출 API", description = "텍스트, 이메일, 이미지에서 일정/할일 추출")
@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
@Slf4j
public class ExtractionApiController {

    private final ExtractionService extractionService;
    private final OllamaService ollamaService;

    @PostMapping("/ask")
    public String askOllama(@RequestBody String prompt) {
        return ollamaService.getOllamaResponse(prompt, "TEXT");
    }

    @Operation(summary = "텍스트에서 일정/할일 추출",
            description = "자연어 텍스트에서 Duckling + LLM을 이용해 일정과 할일을 추출합니다")
    @PostMapping("/text")
    public ResponseEntity<ApiResponseDto<ExtractionDto.Response.ExtractionResult>> extractFromText(
            @RequestBody @Valid ExtractionDto.Request.TextExtractionRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        log.info("텍스트 추출 요청 - 사용자: {}, 텍스트 길이: {}자",
                userDetail.getUserUid(), request.getText().length());

        ExtractionDto.Response.ExtractionResult result =
                extractionService.extractFromText(request, userDetail.getUserUid());

        return ResponseEntity.ok(ApiResponseDto.success(result, "텍스트 추출이 완료되었습니다."));
    }

    @Operation(summary = "이메일 본문에서 일정/할일 추출",
            description = "이메일 본문을 요약하고 일정과 할일을 추출합니다")
    @PostMapping("/email")
    public ResponseEntity<ApiResponseDto<ExtractionDto.Response.ExtractionResult>> extractFromEmail(
            @RequestBody @Valid ExtractionDto.Request.EmailExtractionRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        log.info("이메일 추출 요청 - 사용자: {}, 제목: {}",
                userDetail.getUserUid(), request.getSubject());

        ExtractionDto.Response.ExtractionResult result =
                extractionService.extractFromEmail(request, userDetail.getUserUid());

        return ResponseEntity.ok(ApiResponseDto.success(result, "이메일 추출이 완료되었습니다."));
    }

    @Operation(summary = "추출 미리보기",
            description = "저장하지 않고 추출 결과만 미리보기")
    @PostMapping("/preview")
    public ResponseEntity<ApiResponseDto<ExtractionDto.Response.ExtractionPreview>> previewExtraction(
            @RequestBody @Valid ExtractionDto.Request.TextExtractionRequest request) {

        ExtractionDto.Response.ExtractionPreview result =
                extractionService.previewExtraction(request);

        return ResponseEntity.ok(ApiResponseDto.success(result, "추출 미리보기가 완료되었습니다."));
    }
}