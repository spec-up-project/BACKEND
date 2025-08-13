package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.service.CalendarService;
import com.neekly_report.whirlwind.dto.ApiResponseDto;
import com.neekly_report.whirlwind.dto.CalendarDto;
import com.neekly_report.whirlwind.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "캘린더 API", description = "일정 조회 및 관리")
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarApiController {

    private final CalendarService calendarService;

    @Operation(summary = "내 일정 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CalendarDto.Response.CalendarEvent>>> getMyEvents(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<CalendarDto.Response.CalendarEvent> events =
                calendarService.getUserEvents(userDetail.getTUserUid());

        return ResponseEntity.ok(ApiResponseDto.success(events));
    }

    @Operation(summary = "기간별 일정 조회")
    @GetMapping("/range")
    public ResponseEntity<ApiResponseDto<List<CalendarDto.Response.CalendarEvent>>> getEventsByRange(
            @Parameter(description = "시작일", example = "2024-08-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일", example = "2024-08-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<CalendarDto.Response.CalendarEvent> events =
                calendarService.getEventsByDateRange(
                        userDetail.getTUserUid(),
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59));

        return ResponseEntity.ok(ApiResponseDto.success(events));
    }

    @Operation(summary = "키워드로 일정 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<List<CalendarDto.Response.CalendarEvent>>> searchEvents(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<CalendarDto.Response.CalendarEvent> events =
                calendarService.searchEvents(userDetail.getTUserUid(), keyword);

        return ResponseEntity.ok(ApiResponseDto.success(events));
    }
}
