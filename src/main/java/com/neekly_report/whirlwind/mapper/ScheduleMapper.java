package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.entity.Schedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    Schedule toEntity(ScheduleDto.Request.ScheduleCreateRequest dto);

    Schedule toEntity(ScheduleDto.Request.ScheduleUpdateRequest dto);

    Schedule toEntity(ScheduleDto.Request.ModifyExtractedScheduleRequest dto);

    ScheduleDto.Response.ScheduleResponse toResponse(Schedule entity);

    ScheduleDto.Response.ExtractedSchedulePreview toPreview(Schedule entity);
}
