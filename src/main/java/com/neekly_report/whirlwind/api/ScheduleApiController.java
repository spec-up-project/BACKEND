package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.ExtractionService;
import com.neekly_report.whirlwind.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleApiController {

    private final ScheduleService scheduleService;
    private final ExtractionService extractionService;

    @Operation(summary = "캘린더 자동 일정 Duckling 시간 추출 저장 ")
    @PostMapping
    public ResponseEntity<ExtractionDto.Response.ExtractionResult> createScheduleAuto(
            @RequestBody ScheduleDto.Request.ScheduleCreateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ExtractionDto.Response.ExtractionResult schedule = extractionService.extractDatetimeFromText(request.getRawText(), userDetail.getUserUid());
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "사용자별 캘린더 조회 ")
    @GetMapping
    public ResponseEntity<List<ScheduleDto.Response.ScheduleResponse>> getUserSchedules(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.ScheduleResponse> schedules = scheduleService.getUserSchedules(userDetail.getUserUid());
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "사용자별 캘린더 상세 조회 ")
    @GetMapping("detail/{scheduleUid}")
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> getUserSchedulesDetail(
            @PathVariable String scheduleUid,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {
        ScheduleDto.Response.ScheduleResponse schedules = scheduleService.getUserSchedulesDetail(scheduleUid, userDetail.getUserUid());
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "캘린더 수동 일정 저장")
    @PostMapping("insert")
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> insertSchedule(
            @RequestBody @Valid ScheduleDto.Request.ScheduleCreateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ScheduleDto.Response.ScheduleResponse schedules = scheduleService.insertSchedules(userDetail.getUserUid(), request);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "캘린더 수동 일정 수정")
    @PostMapping("update")
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> updateSchedule(
            @RequestBody @Valid ScheduleDto.Request.ScheduleUpdateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ScheduleDto.Response.ScheduleResponse schedules = scheduleService.updateSchedules(userDetail.getUserUid(), request);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "캘린더 수동 일정 삭제")
    @DeleteMapping("delete/{scheduleUid}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable String scheduleUid,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {
        return ResponseEntity.ok(scheduleService.deleteSchedules(scheduleUid, userDetail.getUserUid()));
    }
}

