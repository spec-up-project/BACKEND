package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DucklingService ducklingService;

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleDto.Response.ScheduleResponse createSchedule(String tUserUid, ScheduleDto.Request.ScheduleCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // Duckling으로 시간 추출
        List<DucklingService.DateTimeInfo> times = ducklingService.extractDateTime(dto.getRawText(), "ko_KR");

        LocalDateTime start = dto.getStartTime();
        LocalDateTime end = dto.getEndTime();

        if (!times.isEmpty()) {
            DucklingService.DateTimeInfo info = times.get(0); // 첫 번째 시간 정보 사용
            start = info.getStart();
            end = info.getEnd();
        }

        Schedule schedule = Schedule.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startTime(start)
                .endTime(end)
                .rawText(dto.getRawText())
                .source(dto.getSource())
                .user(user)
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        return ScheduleDto.Response.ScheduleResponse.builder()
                .tScheduleUid(saved.getTScheduleUid())
                .title(saved.getTitle())
                .content(saved.getContent())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .rawText(saved.getRawText())
                .source(saved.getSource())
                .createDate(saved.getCreateDate())
                .modifyDate(saved.getModifyDate())
                .build();
    }

    public List<ScheduleDto.Response.ScheduleResponse> getUserSchedules(String tUserUid) {
        return scheduleRepository.findByUser_tUserUid(tUserUid)
                .stream()
                .map(s -> ScheduleDto.Response.ScheduleResponse.builder()
                        .tScheduleUid(s.getTScheduleUid())
                        .title(s.getTitle())
                        .content(s.getContent())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .rawText(s.getRawText())
                        .source(s.getSource())
                        .createDate(s.getCreateDate())
                        .modifyDate(s.getModifyDate())
                        .build())
                .collect(Collectors.toList());
    }

    public ScheduleDto.Response.ScheduleResponse insertSchedules(String tUserUid, ScheduleDto.Request.ScheduleCreateRequest request) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .rawText(request.getRawText())
                .source(request.getSource())
                .user(user)
                .build();
        Schedule result = scheduleRepository.save(schedule);
        return ScheduleDto.Response.ScheduleResponse.builder()
                .tScheduleUid(result.getTScheduleUid())
                .title(result.getTitle())
                .content(result.getContent())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .rawText(result.getRawText())
                .source(result.getSource())
                .createDate(result.getCreateDate())
                .modifyDate(result.getModifyDate())
                .build();
    }

    public ScheduleDto.Response.ScheduleResponse updateSchedules(String tUserUid, ScheduleDto.Request.ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findBytScheduleUidAndUser_TUserUid(request.getTScheduleUid(), tUserUid);

        Schedule result = scheduleRepository.save(schedule);

        return ScheduleDto.Response.ScheduleResponse.builder()
                .tScheduleUid(result.getTScheduleUid())
                .title(result.getTitle())
                .content(result.getContent())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .rawText(result.getRawText())
                .source(result.getSource())
                .createDate(result.getCreateDate())
                .modifyDate(result.getModifyDate())
                .build();
    }

    public String deleteSchedules(String tUserUid) {
        try {
            scheduleRepository.deleteById(tUserUid);
            return tUserUid;
        } catch (Exception e) {
            return e.getStackTrace()[0].toString() + " : " + e.getMessage() + " : " + tUserUid;
        }
    }
}