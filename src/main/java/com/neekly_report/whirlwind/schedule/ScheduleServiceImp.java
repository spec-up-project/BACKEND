package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.entity.Schedule;

import java.util.List;

public interface ScheduleServiceImp {
    List<Schedule> getPlanList(String tUserUid);

    Schedule createSchedule(ScheduleDto.Request.ScheduleCreateRequest request);

    List<Schedule> getUserSchedules(String tUserUid);
}
