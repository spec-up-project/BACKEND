package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CategoryDto;
import com.neekly_report.whirlwind.dto.ScheduleDto.Response.CalendarEvent;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.entity.Category;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.mapper.CategoryMapper;
import com.neekly_report.whirlwind.mapper.ScheduleMapper;
import com.neekly_report.whirlwind.repository.CategoryRepository;
import com.neekly_report.whirlwind.repository.ScheduleRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final ScheduleMapper scheduleMapper;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public ScheduleDto.Response.ScheduleResponse insertSchedules(String userUid, ScheduleDto.Request.ScheduleManualCreateRequest request) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Category category = categoryRepository.findByCategoryUidAndUser_userUid(request.getCategoryUid(), userUid);

        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setUser(user);
        schedule.setCategory(category);

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleDto.Response.ScheduleResponse updateSchedules(String userUid, ScheduleDto.Request.ScheduleUpdateRequest request) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Category category = categoryRepository.findByCategoryUidAndUser_userUid(request.getCategoryUid(), userUid);

        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setUser(user);
        schedule.setCategory(category);

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
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

        List<Category> categoryList = categoryRepository.findByUser_userUidOrderByCreateDateAsc(userUid);
        List<CategoryDto.Response.CategoryResponse> categoryResponses = categoryList.stream().map(categoryMapper::toResponse).toList();

        return scheduleRepository.findByUser_userUid(userUid)
                .stream()
                .map(schedule -> {
                    ScheduleDto.Response.ScheduleResponse response = scheduleMapper.toResponse(schedule);
                    response.setCategory(traceToRoot(response.getCategoryUid(), categoryResponses));
                    return response;
                })
                .toList();
    }

    public ScheduleDto.Response.ScheduleResponse getUserSchedulesDetail(String scheduleUid, String userUid) {
        Schedule schedule = scheduleRepository.findByScheduleUidAndUser_userUid(scheduleUid, userUid);

        List<Category> categoryList = categoryRepository.findByUser_userUidOrderByCreateDateAsc(userUid);
        List<CategoryDto.Response.CategoryResponse> categoryResponses = categoryList.stream().map(categoryMapper::toResponse).toList();

        ScheduleDto.Response.ScheduleResponse responses = scheduleMapper.toResponse(schedule);
        responses.setCategory(traceToRoot(schedule.getCategory().getCategoryUid(), categoryResponses));

        return responses;
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


    private List< CategoryDto.Response.CategoryResponse> traceToRoot(String targetUid, List< CategoryDto.Response.CategoryResponse> flatList) {
        Map<String,  CategoryDto.Response.CategoryResponse> map = new HashMap<>();
        for ( CategoryDto.Response.CategoryResponse category : flatList) {
            map.put(category.getCategoryUid(), category);
        }

        List< CategoryDto.Response.CategoryResponse> path = new ArrayList<>();
        CategoryDto.Response.CategoryResponse current = map.get(targetUid);

        while (current != null) {
            path.add(current);
            String parentUid = current.getParentUid();
            current = (parentUid != null && !parentUid.isEmpty()) ? map.get(parentUid) : null;
        }

        // 루트부터 출력
        Collections.reverse(path);

        return path;
    }

}