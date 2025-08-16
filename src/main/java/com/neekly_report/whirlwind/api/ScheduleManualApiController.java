package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.service.ScheduleService;
import com.neekly_report.whirlwind.dto.ApiResponseDto;
import com.neekly_report.whirlwind.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "수동 일정 API", description = "수동 일정 조회 및 관리")
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleManualApiController {

    private final ScheduleService scheduleService;

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

    @Operation(summary = "내 일정 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ScheduleDto.Response.CalendarEvent>>> getMyEvents(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.CalendarEvent> events =
                scheduleService.getUserEvents(userDetail.getUserUid());

        return ResponseEntity.ok(ApiResponseDto.success(events));
    }

    @Operation(summary = "기간별 일정 조회")
    @GetMapping("/range")
    public ResponseEntity<ApiResponseDto<List<ScheduleDto.Response.CalendarEvent>>> getEventsByRange(
            @Parameter(description = "시작일", example = "2024-08-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일", example = "2024-08-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.CalendarEvent> events =
                scheduleService.getEventsByDateRange(
                        userDetail.getUserUid(),
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59));

        return ResponseEntity.ok(ApiResponseDto.success(events));
    }

    @Operation(summary = "키워드로 일정 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<List<ScheduleDto.Response.CalendarEvent>>> searchEvents(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.CalendarEvent> events =
                scheduleService.searchEvents(userDetail.getUserUid(), keyword);

        return ResponseEntity.ok(ApiResponseDto.success(events));
    }

}
