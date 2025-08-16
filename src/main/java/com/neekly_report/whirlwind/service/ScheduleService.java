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

    public List<ScheduleDto.Response.ScheduleResponse> getUserSchedules(String userUid) {
        return scheduleRepository.findByUser_userUid(userUid)
                .stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ScheduleDto.Response.ScheduleResponse getUserSchedulesDetail(String scheduleUid, String userUid) {
        Schedule schedule = scheduleRepository.findByScheduleUidAndUser_userUid(scheduleUid, userUid);
        return scheduleMapper.toResponse(schedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto.Response.ScheduleResponse insertSchedules(String userUid, ScheduleDto.Request.ScheduleCreateRequest request) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setUser(user);

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleDto.Response.ScheduleResponse updateSchedules(String userUid, ScheduleDto.Request.ScheduleUpdateRequest request) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        request.setUser(user);

        return scheduleMapper.toResponse(scheduleRepository.save(scheduleMapper.toEntity(request)));
    }

    @Transactional
    public String deleteSchedules(String scheduleUid, String userUid) {
        try {
            scheduleRepository.deleteByScheduleUidAndUser_userUid(scheduleUid, userUid);
            return userUid;
        } catch (Exception e) {
            return e.getStackTrace()[0].toString() + " : " + e.getMessage() + " : " + userUid;
        }
    }
}
