package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.WeeklyReportDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.WeeklyReportService;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class WeeklyReportApiController {
    private final WeeklyReportService weeklyReportService;

    /**
     * 주간 팀 보고서 생성
     */
    @GetMapping
    public ResponseEntity<WeeklyReportDto.Response.WeeklyReport> makeMainWeeklyReport(@RequestBody WeeklyReportDto.Request.WeeklyReportPreview request,
                                                                                      @AuthenticationPrincipal UserDto.UserDetail userDetail) {
        WeeklyReportDto.Response.WeeklyReport report = weeklyReportService.generateWeeklyReport(userDetail.getUserUid(), request.getChat());
        return ResponseEntity.ok(report);
    }

    /**
     * 주간 보고서 (json 형식)
     */
    @PostMapping("/make")
    public ResponseEntity<WeeklyReportDto.Response.TextReport> makeReport(@RequestBody @Valid WeeklyReportDto.Request.TextReport textReport,
                                                                          @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        return ResponseEntity.ok(weeklyReportService.makeReport(textReport, userDetail.getUserUid()));
    }

    @PostMapping("/text")
    public ResponseEntity<WeeklyReportDto.Response.SaveResponse> createReport(@RequestBody WeeklyReportDto.Request.SaveRequest request) {
        return ResponseEntity.ok(weeklyReportService.createReport(request));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyReportDto.Response.SaveResponse>> getWeeklyReports(@RequestParam String userUid) {
        return ResponseEntity.ok(weeklyReportService.getReportsByUser(userUid));
    }

    @PutMapping("/{reportUid}/categorize")
    public ResponseEntity<WeeklyReportDto.Response.SaveResponse> updateCategory(
            @PathVariable String reportUid,
            @RequestParam String mainCategoryUid,
            @RequestParam String subCategoryUid
    ) {
        return ResponseEntity.ok(weeklyReportService.updateCategory(reportUid, mainCategoryUid, subCategoryUid));
    }

    /**
     * 주간 팀 보고서 (Markdown 형식)
     */
    @GetMapping("/weekly/markdown")
    public ResponseEntity<WeeklyReportDto.Response.WeeklyReport> getWeeklyMarkdownReport(@RequestParam String userId) {
        WeeklyReportDto.Response.WeeklyReport report = weeklyReportService.generateMarkdown(userId);
        return ResponseEntity.ok(report);
    }

    /**
     * 주간 팀 보고서 (Excel 다운로드)
     */
    @GetMapping("/weekly/excel")
    public ResponseEntity<Resource> downloadWeeklyExcelReport(@RequestParam String userId) {
        try {
            File excelFile = weeklyReportService.generateWeeklyReportExcel(userId);
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