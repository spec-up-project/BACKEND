package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.ReportDto;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    WeeklyReport toEntity(ReportDto.Request.TextReport textReport);
    ReportDto.Response.TextReport toResponse(WeeklyReport entity);
}
