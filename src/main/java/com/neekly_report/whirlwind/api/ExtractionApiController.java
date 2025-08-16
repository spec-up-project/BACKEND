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

@Tag(name = "추출 테스트 API")
@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
@Slf4j
public class ExtractionApiController {

    private final ExtractionService extractionService;
    private final OllamaService ollamaService;

    @Operation(summary = "LLM 연결 테스트")
    @PostMapping("/ask")
    public String askOllama(@RequestBody String prompt) {
        return ollamaService.getOllamaResponse(prompt, "TEXT");
    }

    @Operation(summary = "추출 미리보기",
            description = "저장하지 않고 추출 결과만 미리보기")
    @PostMapping("/preview")
    public ResponseEntity<ExtractionDto.Response.ExtractionPreview> previewExtraction(
            @RequestBody @Valid ExtractionDto.Request.TextExtractionRequest request) {

        ExtractionDto.Response.ExtractionPreview result =
                extractionService.previewExtraction(request);

        return ResponseEntity.ok(result);
    }
}