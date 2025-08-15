package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.CalendarDto;
import com.neekly_report.whirlwind.entity.Schedule;
import org.springframework.stereotype.Component;

@Component
public class CalendarMapper {

    public CalendarDto.Response.CalendarEvent toCalendarEvent(Schedule schedule) {
        return CalendarDto.Response.CalendarEvent.builder()
                .scheduleId(schedule.getScheduleUid())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .source(schedule.getSource())
                .rawText(schedule.getRawText())
                .createDate(schedule.getCreateDate())
                .modifyDate(schedule.getModifyDate())
                .build();
    }

    public CalendarDto.Response.CalendarEventPreview toEventPreview(Schedule schedule) {
        return CalendarDto.Response.CalendarEventPreview.builder()
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .rawText(schedule.getRawText())
                .build();
    }
}