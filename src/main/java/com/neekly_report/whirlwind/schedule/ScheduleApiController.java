package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.user.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleApiController {
    private final ScheduleService scheduleService;

    @Operation(summary = "자연어로 일정 생성", description = "자유 텍스트로부터 생성된 일정을 등록합니다.")
    @PostMapping
    public ResponseEntity<ScheduleDTO.Response.ScheduleResponse> createSchedule(
            @RequestBody @Valid ScheduleDTO.Request.ScheduleCreateRequest request,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {
        ScheduleDTO.Response.ScheduleResponse schedule = scheduleService.createSchedule(userDetail.getTUserUid(), request);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "내 일정 전체 조회")
    @GetMapping
    public ResponseEntity<List<ScheduleDTO.Response.ScheduleResponse>> getUserSchedules(
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {
        List<ScheduleDTO.Response.ScheduleResponse> schedules = scheduleService.getUserSchedules(userDetail.getTUserUid());
        return ResponseEntity.ok(schedules);
    }

}
