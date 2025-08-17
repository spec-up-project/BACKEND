package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.WeeklyReportDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import com.neekly_report.whirlwind.service.WeeklyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Tag(name = "리포트 생성 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class WeeklyReportApiController {
    private final WeeklyReportService weeklyReportService;

    /**
     * 주간 팀 보고서 생성
     */
    @Operation(summary = "채팅용 자동 주간보고 생성")
    @PostMapping("chat")
    public ResponseEntity<String> requestMakeReport(@RequestBody WeeklyReportDto.Request.WeeklyReportPreview request,
                                                                                         @AuthenticationPrincipal UserDto.UserDetail userDetail) {
        return ResponseEntity.ok(weeklyReportService.requestReport(userDetail.getUserUid(), request.getChat()));
    }

    /**
     * 주간 보고서 (json 형식)
     */
    @Operation(summary = "팝업 내 자동 주간보고 생성")
    @PostMapping("make")
    public ResponseEntity<WeeklyReportDto.Response.TextReport> makeReport(@RequestBody WeeklyReportDto.Request.TextReport textReport,
                                                                          @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        return ResponseEntity.ok(weeklyReportService.makeReport(textReport, userDetail.getUserUid()));
    }

    @Operation(summary = "전체 주간보고 조회")
    @GetMapping
    public ResponseEntity<List<WeeklyReportDto.Response.SaveResponse>> getWeeklyReports(@AuthenticationPrincipal UserDto.UserDetail userDetail) {
        return ResponseEntity.ok(weeklyReportService.getReportsByUser(userDetail.getUserUid()));
    }

    @Operation(summary = "주간보고 상세 조회")
    @GetMapping(value = "detail/{reportUid}")
    public ResponseEntity<WeeklyReportDto.Response.WeeklyReportDetail> getWeeklyReports(@PathVariable String reportUid,
                                                                                        @AuthenticationPrincipal UserDto.UserDetail userDetail) {
        return ResponseEntity.ok(weeklyReportService.getReportsByUid(reportUid));
    }


    /**
     * 주간 팀 보고서 (Markdown 형식)
     */
    @Operation(summary = "주간 팀 보고서 (Markdown 형식) - 미완성")
    @GetMapping("markdown")
    public ResponseEntity<WeeklyReportDto.Response.WeeklyReportResult> getWeeklyMarkdownReport(@AuthenticationPrincipal UserDto.UserDetail userDetail) {
        WeeklyReportDto.Response.WeeklyReportResult report = weeklyReportService.generateMarkdown(userDetail.getUserUid());
        return ResponseEntity.ok(report);
    }

    /**
     * 주간 팀 보고서 (Excel 다운로드)
     */
    @Operation(summary = "주간 팀 보고서 (Excel 다운로드) - 미완성")
    @GetMapping("excel")
    public ResponseEntity<Resource> downloadWeeklyExcelReport(@AuthenticationPrincipal UserDto.UserDetail userDetail) {
        try {
            File excelFile = weeklyReportService.generateWeeklyReportExcel(userDetail.getUserUid());
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