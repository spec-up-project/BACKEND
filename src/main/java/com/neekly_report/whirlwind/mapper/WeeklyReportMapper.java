package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.WeeklyReportDto;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeeklyReportMapper {

    WeeklyReport toEntity(WeeklyReportDto.Request.TextReport textReport);
    WeeklyReportDto.Response.TextReport toTextReport (WeeklyReport entity);

    @Mapping(target = "mainCategory", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    WeeklyReport toEntity(WeeklyReportDto.Request.SaveRequest request);

    @Mapping(source = "mainCategory.categoryName", target = "mainCategoryName")
    @Mapping(source = "subCategory.categoryName", target = "subCategoryName")
    WeeklyReportDto.Response.SaveResponse toSaveResponse(WeeklyReport report);
}
