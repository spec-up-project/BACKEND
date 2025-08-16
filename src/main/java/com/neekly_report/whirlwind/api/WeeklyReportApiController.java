package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.ReportDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class WeeklyReportApiController {
    private final ReportService reportService;

    /**
     * 주간 보고서 (json 형식)
     */
    @GetMapping("/make")
    public ResponseEntity<ReportDto.Response.TextReport> makeReport(@RequestBody @Valid ReportDto.Request.TextReport textReport,
                                                                      @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        return ResponseEntity.ok(reportService.makeReport(textReport, userDetail.getUserUid()));
    }

    /**
     * 주간 팀 보고서 (Markdown 형식)
     */
    @GetMapping("/weekly/markdown")
    public ResponseEntity<ReportDto.Response.WeeklyReport> getWeeklyMarkdownReport(@RequestParam String userId) {
        ReportDto.Response.WeeklyReport report = reportService.generateWeeklyReport(userId);
        return ResponseEntity.ok(report);
    }

    /**
     * 주간 팀 보고서 (Excel 다운로드)
     */
    @GetMapping("/weekly/excel")
    public ResponseEntity<Resource> downloadWeeklyExcelReport(@RequestParam String userId) {
        try {
            File excelFile = reportService.generateWeeklyReportExcel(userId);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}