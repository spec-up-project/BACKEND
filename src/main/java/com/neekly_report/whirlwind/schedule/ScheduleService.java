package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleDTO.Response.ScheduleResponse createSchedule(String tUserUid, ScheduleDTO.Request.ScheduleCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Schedule schedule = Schedule.builder()
                .title(dto.getTitle())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .user(user)
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        return ScheduleDTO.Response.ScheduleResponse.builder()
                .tScheduleUid(saved.getTScheduleUid())
                .title(saved.getTitle())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .createDate(saved.getCreateDate())
                .build();
    }

    public List<ScheduleDTO.Response.ScheduleResponse> getUserSchedules(String tUserUid) {
        return scheduleRepository.findByUser_tUserUid(tUserUid)
                .stream()
                .map(s -> ScheduleDTO.Response.ScheduleResponse.builder()
                        .tScheduleUid(s.getTScheduleUid())
                        .title(s.getTitle())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .createDate(s.getCreateDate())
                        .build())
                .collect(Collectors.toList());
    }
}