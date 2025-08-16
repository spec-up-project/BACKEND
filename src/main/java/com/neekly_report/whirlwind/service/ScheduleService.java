package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.ScheduleDto.Response.CalendarEvent;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.mapper.ScheduleMapper;
import com.neekly_report.whirlwind.repository.ScheduleRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleMapper scheduleMapper;

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

    public List<ScheduleDto.Response.ScheduleResponse> getUserSchedules(String userUid) {
        log.info("사용자 일정 조회 - 사용자ID: {}", userUid);
        return scheduleRepository.findByUser_userUid(userUid)
                .stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ScheduleDto.Response.ScheduleResponse getUserSchedulesDetail(String scheduleUid, String userUid) {
        Schedule schedule = scheduleRepository.findByScheduleUidAndUser_userUid(scheduleUid, userUid);
        return scheduleMapper.toResponse(schedule);
    }

    /**
     * 기간별 일정 조회
     */
    public List<CalendarEvent> getSchedulesByDateRange(
            String userId, LocalDateTime startDate, LocalDateTime endDate) {

        log.info("기간별 일정 조회 - 사용자ID: {}, 기간: {} ~ {}", userId, startDate, endDate);

        List<Schedule> schedules = scheduleRepository.findByUser_userUidAndStartTimeBetween(
                userId, startDate, endDate);

        return schedules.stream()
                .map(Schedule::toScheduleEvent)
                .sorted(Comparator.comparing(
                        ScheduleDto.Response.CalendarEvent::getStartTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    /**
     * 키워드로 일정 검색
     */
    public List<CalendarEvent> searchEvents(String userId, String keyword) {
        log.info("일정 검색 - 사용자ID: {}, 키워드: {}", userId, keyword);

        List<Schedule> schedules = scheduleRepository.findByUser_userUidAndTitleContainingOrContentContaining(
                userId, keyword, keyword);

        return schedules.stream()
                .map(Schedule::toScheduleEvent)
                .sorted(Comparator.comparing(
                        ScheduleDto.Response.CalendarEvent::getStartTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    /**
     * 오늘의 일정 조회
     */
    public List<CalendarEvent> getTodayEvents(String userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        return getSchedulesByDateRange(userId, startOfDay, endOfDay);
    }

    /**
     * 이번 주 일정 조회
     */
    public List<CalendarEvent> getThisWeekEvents(String userId) {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        return getSchedulesByDateRange(userId, startOfWeek, endOfWeek);
    }

    /**
     * 다가오는 일정 조회 (다음 7일)
     */
    public List<CalendarEvent> getUpcomingEvents(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekLater = now.plusDays(7);

        return getSchedulesByDateRange(userId, now, weekLater);
    }

    private Boolean isAllDayEvent(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) return false;

        return startTime.getHour() == 0 && startTime.getMinute() == 0
                && endTime.getHour() == 23 && endTime.getMinute() == 59;
    }
}