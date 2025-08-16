package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.ScheduleDto.Response.CalendarEvent;
import com.neekly_report.whirlwind.dto.WeeklyReportDto.Response.WeeklySummary;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelReportGenerator {

    public File generateWeeklyExcel(String userId, LocalDate start, LocalDate end,
                                    List<CalendarEvent> events, WeeklySummary summary) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Weekly Report");

        // 사용자 정보
        sheet.createRow(0).createCell(0).setCellValue("User ID");
        sheet.getRow(0).createCell(1).setCellValue(userId);
        sheet.createRow(1).createCell(0).setCellValue("Report Period");
        sheet.getRow(1).createCell(1).setCellValue(start + " ~ " + end);

        // 일정
        int rowIdx = 3;
        sheet.createRow(rowIdx++).createCell(0).setCellValue("Schedule");
        sheet.createRow(rowIdx++).createCell(0).setCellValue("Title");
        sheet.getRow(rowIdx - 1).createCell(1).setCellValue("Start Time");
        for (CalendarEvent event : events) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(event.getTitle());
            row.createCell(1).setCellValue(event.getStartTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")));
        }

        // 요약
        rowIdx++;
        sheet.createRow(rowIdx++).createCell(0).setCellValue("Summary");
        sheet.createRow(rowIdx++).createCell(0).setCellValue("Completion Rate (%)");
        sheet.getRow(rowIdx - 1).createCell(1).setCellValue(summary.getCompletionRate());
        sheet.createRow(rowIdx++).createCell(0).setCellValue("Productivity Score");
        sheet.getRow(rowIdx - 1).createCell(1).setCellValue(summary.getProductivityScore());

        // 저장
        String filename = "주간보고" + userId + "_" + start + "_to_" + end + ".xlsx";
        File file = new File("reports/" + filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
        return file;
    }
}

