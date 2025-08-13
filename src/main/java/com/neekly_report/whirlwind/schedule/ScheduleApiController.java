package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.UserDto;
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
    private final NLPCalendarService nlpCalendarService;

    @Operation(summary = "캘린더 자동 일정 Duckling 시간 추출 저장 ")
    @PostMapping
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> createSchedule(
            @RequestBody @Valid ScheduleDto.Request.ScheduleCreateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ScheduleDto.Response.ScheduleResponse schedule = scheduleService.createSchedule(userDetail.getTUserUid(), request);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "사용자별 캘린더 조회 ")
    @GetMapping
    public ResponseEntity<List<ScheduleDto.Response.ScheduleResponse>> getUserSchedules(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.ScheduleResponse> schedules = scheduleService.getUserSchedules(userDetail.getTUserUid());
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "캘린더 수동 일정 저장")
    @PostMapping("insert")
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> insertSchedule(
            @RequestBody @Valid ScheduleDto.Request.ScheduleCreateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ScheduleDto.Response.ScheduleResponse schedules = scheduleService.insertSchedules(userDetail.getTUserUid(), request);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "캘린더 수동 일정 수정")
    @PutMapping("update")
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> updateSchedule(
            @RequestBody @Valid ScheduleDto.Request.ScheduleUpdateRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ScheduleDto.Response.ScheduleResponse schedules = scheduleService.updateSchedules(userDetail.getTUserUid(), request);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "캘린더 수동 일정 삭제")
    @DeleteMapping("delete")
    public ResponseEntity<String> deleteSchedule(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {
        return ResponseEntity.ok(scheduleService.deleteSchedules(userDetail.getTUserUid()));
    }

    @PostMapping("/nlp/text")
    public ResponseEntity<List<ScheduleDto.Response.ScheduleResponse>> extractFromText(
            @RequestBody @Valid ScheduleDto.Request.TextBasedScheduleRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Request.ScheduleCreateRequest> extracted = nlpCalendarService.extractSchedulesFromText(request.getText(), "TEXT");
        List<ScheduleDto.Response.ScheduleResponse> saved = extracted.stream()
                .map(schedule -> scheduleService.createSchedule(userDetail.getTUserUid(), schedule))
                .collect(Collectors.toList());

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/nlp/preview")
    public ResponseEntity<List<ScheduleDto.Response.ExtractedSchedulePreview>> previewExtractedSchedules(
            @RequestBody @Valid ScheduleDto.Request.TextBasedScheduleRequest request) {

        List<ScheduleDto.Request.ScheduleCreateRequest> extracted = nlpCalendarService.extractSchedulesFromText(request.getText(), "TEXT");
        List<ScheduleDto.Response.ExtractedSchedulePreview> preview = extracted.stream()
                .map(schedule -> ScheduleDto.Response.ExtractedSchedulePreview.builder()
                        .title(schedule.getTitle())
                        .content(schedule.getContent())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .rawText(schedule.getRawText())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(preview);
    }

    @PostMapping("/nlp/save-modified")
    public ResponseEntity<ScheduleDto.Response.ScheduleResponse> saveModifiedSchedule(
            @RequestBody @Valid ScheduleDto.Request.ModifyExtractedScheduleRequest request,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        ScheduleDto.Request.ScheduleCreateRequest scheduleRequest = ScheduleDto.Request.ScheduleCreateRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .rawText(request.getRawText())
                .source(request.getSource())
                .build();

        ScheduleDto.Response.ScheduleResponse saved = scheduleService.createSchedule(userDetail.getTUserUid(), scheduleRequest);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/nlp/save-batch")
    public ResponseEntity<List<ScheduleDto.Response.ScheduleResponse>> saveBatchSchedules(
            @RequestBody @Valid List<ScheduleDto.Request.ModifyExtractedScheduleRequest> requests,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<ScheduleDto.Response.ScheduleResponse> saved = requests.stream()
                .map(request -> {
                    ScheduleDto.Request.ScheduleCreateRequest scheduleRequest = ScheduleDto.Request.ScheduleCreateRequest.builder()
                            .title(request.getTitle())
                            .content(request.getContent())
                            .startTime(request.getStartTime())
                            .endTime(request.getEndTime())
                            .rawText(request.getRawText())
                            .source(request.getSource())
                            .build();
                    return scheduleService.createSchedule(userDetail.getTUserUid(), scheduleRequest);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(saved);
    }
}

