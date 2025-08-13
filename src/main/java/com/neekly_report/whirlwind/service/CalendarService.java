package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CalendarDto;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CalendarService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 사용자의 모든 일정 조회
     */
    public List<CalendarDto.Response.CalendarEvent> getUserEvents(String userId) {
        log.info("사용자 일정 조회 - 사용자ID: {}", userId);

        List<Schedule> schedules = scheduleRepository.findByUser_tUserUid(userId);

        return schedules.stream()
                .map(this::toCalendarEvent)
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();
    }

    /**
     * 기간별 일정 조회
     */
    public List<CalendarDto.Response.CalendarEvent> getEventsByDateRange(
            String userId, LocalDateTime startDate, LocalDateTime endDate) {

        log.info("기간별 일정 조회 - 사용자ID: {}, 기간: {} ~ {}", userId, startDate, endDate);

        List<Schedule> schedules = scheduleRepository.findByUser_tUserUidAndStartTimeBetween(
                userId, startDate, endDate);

        return schedules.stream()
                .map(this::toCalendarEvent)
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();
    }

    /**
     * 키워드로 일정 검색
     */
    public List<CalendarDto.Response.CalendarEvent> searchEvents(String userId, String keyword) {
        log.info("일정 검색 - 사용자ID: {}, 키워드: {}", userId, keyword);

        List<Schedule> schedules = scheduleRepository.findByUser_tUserUidAndTitleContainingOrContentContaining(
                userId, keyword, keyword);

        return schedules.stream()
                .map(this::toCalendarEvent)
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();
    }

    /**
     * 오늘의 일정 조회
     */
    public List<CalendarDto.Response.CalendarEvent> getTodayEvents(String userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        return getEventsByDateRange(userId, startOfDay, endOfDay);
    }

    /**
     * 이번 주 일정 조회
     */
    public List<CalendarDto.Response.CalendarEvent> getThisWeekEvents(String userId) {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        return getEventsByDateRange(userId, startOfWeek, endOfWeek);
    }

    /**
     * 다가오는 일정 조회 (다음 7일)
     */
    public List<CalendarDto.Response.CalendarEvent> getUpcomingEvents(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekLater = now.plusDays(7);

        return getEventsByDateRange(userId, now, weekLater);
    }

    private CalendarDto.Response.CalendarEvent toCalendarEvent(Schedule schedule) {
        return CalendarDto.Response.CalendarEvent.builder()
                .scheduleId(schedule.getTScheduleUid())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isAllDay(isAllDayEvent(schedule.getStartTime(), schedule.getEndTime()))
                .source(schedule.getSource())
                .rawText(schedule.getRawText())
                .createDate(schedule.getCreateDate())
                .modifyDate(schedule.getModifyDate())
                .build();
    }

    private Boolean isAllDayEvent(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) return false;

        return startTime.getHour() == 0 && startTime.getMinute() == 0
                && endTime.getHour() == 23 && endTime.getMinute() == 59;
    }
}