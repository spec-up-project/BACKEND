package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.ScheduleService;
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
@RequestMapping("/api/schedule/manual")
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

    @Operation(summary = "사용자별 캘린더 조회 ")
    @GetMapping("calendar")
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

    @Operation(summary = "기간별 일정 조회")
    @GetMapping("/range")
    public ResponseEntity<List<ScheduleDto.Response.CalendarEvent>> getSchedulesByRange(
            @Parameter(description = "시작일", example = "2024-08-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일", example = "2024-08-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.CalendarEvent> events =
                scheduleService.getSchedulesByDateRange(
                        userDetail.getUserUid(),
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59));

        return ResponseEntity.ok(events);
    }

    @Operation(summary = "키워드로 일정 검색")
    @GetMapping("/search")
    public ResponseEntity<List<ScheduleDto.Response.CalendarEvent>> searchEvents(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.CalendarEvent> events =
                scheduleService.searchEvents(userDetail.getUserUid(), keyword);

        return ResponseEntity.ok(events);
    }

}
