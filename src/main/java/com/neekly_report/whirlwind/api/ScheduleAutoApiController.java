package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.ExtractionService;
import com.neekly_report.whirlwind.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "자동 일정 API", description = "LLM을 통한 자동 일정 저장 및 수정")
@RestController
@RequestMapping("/api/schedule/auto")
@RequiredArgsConstructor
@Slf4j
public class ScheduleAutoApiController {

    private final ScheduleService scheduleService;
    private final ExtractionService extractionService;

    @Operation(summary = "캘린더 자동 일정 시간 추출 저장 ")
    @PostMapping
    public ResponseEntity<ExtractionDto.Response.ExtractionResult> createScheduleAuto(
            @RequestBody ScheduleDto.Request.ScheduleAutoCreateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ExtractionDto.Response.ExtractionResult schedule = extractionService.extractDatetimeFromText(request.getRawText(), userDetail.getUserUid());
        return ResponseEntity.ok(schedule);
    }

    // TODO : 캘린더 자동 수정
    // TODO : 캘린더 자동 삭제



}

