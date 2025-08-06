package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.user.UserDTO;
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

    @PostMapping
    public ResponseEntity<ScheduleDTO.Response.ScheduleResponse> createSchedule(
            @RequestBody @Valid ScheduleDTO.Request.ScheduleCreateRequest request,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {

        ScheduleDTO.Response.ScheduleResponse schedule = scheduleService.createSchedule(userDetail.getTUserUid(), request);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDTO.Response.ScheduleResponse>> getUserSchedules(
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {

        List<ScheduleDTO.Response.ScheduleResponse> schedules = scheduleService.getUserSchedules(userDetail.getTUserUid());
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/nlp/text")
    public ResponseEntity<List<ScheduleDTO.Response.ScheduleResponse>> extractFromText(
            @RequestBody @Valid ScheduleDTO.Request.TextBasedScheduleRequest request,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {

        List<ScheduleDTO.Request.ScheduleCreateRequest> extracted = nlpCalendarService.extractSchedulesFromText(request.getText(), "TEXT");
        List<ScheduleDTO.Response.ScheduleResponse> saved = extracted.stream()
                .map(schedule -> scheduleService.createSchedule(userDetail.getTUserUid(), schedule))
                .collect(Collectors.toList());

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/nlp/preview")
    public ResponseEntity<List<ScheduleDTO.Response.ExtractedSchedulePreview>> previewExtractedSchedules(
            @RequestBody @Valid ScheduleDTO.Request.TextBasedScheduleRequest request) {

        List<ScheduleDTO.Request.ScheduleCreateRequest> extracted = nlpCalendarService.extractSchedulesFromText(request.getText(), "TEXT");
        List<ScheduleDTO.Response.ExtractedSchedulePreview> preview = extracted.stream()
                .map(schedule -> ScheduleDTO.Response.ExtractedSchedulePreview.builder()
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
    public ResponseEntity<ScheduleDTO.Response.ScheduleResponse> saveModifiedSchedule(
            @RequestBody @Valid ScheduleDTO.Request.ModifyExtractedScheduleRequest request,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {

        ScheduleDTO.Request.ScheduleCreateRequest scheduleRequest = ScheduleDTO.Request.ScheduleCreateRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .rawText(request.getRawText())
                .source(request.getSource())
                .build();

        ScheduleDTO.Response.ScheduleResponse saved = scheduleService.createSchedule(userDetail.getTUserUid(), scheduleRequest);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/nlp/save-batch")
    public ResponseEntity<List<ScheduleDTO.Response.ScheduleResponse>> saveBatchSchedules(
            @RequestBody @Valid List<ScheduleDTO.Request.ModifyExtractedScheduleRequest> requests,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {

        List<ScheduleDTO.Response.ScheduleResponse> saved = requests.stream()
                .map(request -> {
                    ScheduleDTO.Request.ScheduleCreateRequest scheduleRequest = ScheduleDTO.Request.ScheduleCreateRequest.builder()
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

