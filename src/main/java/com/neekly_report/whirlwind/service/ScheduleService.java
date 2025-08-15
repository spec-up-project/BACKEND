package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.mapper.ScheduleMapper;
import com.neekly_report.whirlwind.repository.ScheduleRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DucklingService ducklingService;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleDto.Response.ScheduleResponse createSchedule(String tUserUid, ScheduleDto.Request.ScheduleCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        List<DucklingService.DateTimeInfo> times = ducklingService.extractDateTime(dto.getRawText(), "ko_KR");

        LocalDateTime start = dto.getStartTime();
        LocalDateTime end = dto.getEndTime();

        if (!times.isEmpty()) {
            DucklingService.DateTimeInfo info = times.get(0);
            start = info.getStart();
            end = info.getEnd();
        }

        Schedule schedule = scheduleMapper.toEntity(dto);
        schedule.setUser(user);
        schedule.setStartTime(start);
        schedule.setEndTime(end);

        Schedule saved = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(saved);
    }

    public List<ScheduleDto.Response.ScheduleResponse> getUserSchedules(String tUserUid) {
        return scheduleRepository.findByUser_userUid(tUserUid)
                .stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ScheduleDto.Response.ScheduleResponse getUserSchedulesDetail(String tUserUid, String tScheduleUid) {
        Schedule schedule = scheduleRepository.findByScheduleUidAndUser_userUid(tUserUid, tScheduleUid);
        return scheduleMapper.toResponse(schedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto.Response.ScheduleResponse insertSchedules(String tUserUid, ScheduleDto.Request.ScheduleCreateRequest request) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setUser(user);

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleDto.Response.ScheduleResponse updateSchedules(String tUserUid, ScheduleDto.Request.ScheduleUpdateRequest request) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        request.setUser(user);

        return scheduleMapper.toResponse(scheduleRepository.save(scheduleMapper.toEntity(request)));
    }

    @Transactional
    public String deleteSchedules(String tScheduleUid, String tUserUid) {
        try {
            scheduleRepository.deleteByScheduleUidAndUser_userUid(tScheduleUid, tUserUid);
            return tUserUid;
        } catch (Exception e) {
            return e.getStackTrace()[0].toString() + " : " + e.getMessage() + " : " + tUserUid;
        }
    }
}
