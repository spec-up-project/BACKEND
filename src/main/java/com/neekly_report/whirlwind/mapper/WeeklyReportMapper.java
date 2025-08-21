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

    WeeklyReportDto.Response.WeeklyReportResult toWeeklyReportResult(WeeklyReport report);


    @Mapping(target = "createDate", source = "createDate")
    @Mapping(target = "modifyDate", source = "modifyDate")
    WeeklyReportDto.Response.WeeklyReportDetail toWeeklyReportDetail(WeeklyReport report);

    WeeklyReport toEntity(WeeklyReportDto.Response.WeeklyReportResult reportResult);
}
