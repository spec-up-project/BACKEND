package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.WeeklyReportDto;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeeklyReportMapper {

    WeeklyReport toEntity(WeeklyReportDto.Request.TextReport textReport);
    WeeklyReportDto.Response.TextReport toTextReport (WeeklyReport entity);

    WeeklyReport toEntity(WeeklyReportDto.Request.SaveRequest request);

    WeeklyReportDto.Response.SaveResponse toSaveResponse(WeeklyReport report);
}
