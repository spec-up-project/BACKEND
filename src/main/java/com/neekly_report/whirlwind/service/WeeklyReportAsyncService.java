package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.WeeklyReportDto;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import com.neekly_report.whirlwind.repository.WeeklyReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyReportAsyncService {
    private final WeeklyReportRepository weeklyReportRepository;

    private final ScheduleService scheduleService;
    private final OllamaService ollamaService;
    private final ExtractionService extractionService;


    @Async
    public void generateContentAsync(String reportUid, String userUid, String chat) {
        LocalDateTime now = LocalDateTime.now();
        log.debug("LLM 비동기 처리 시작 : {}", reportUid);

        WeeklyReport report = weeklyReportRepository.findByReportUid(reportUid);
        if (report == null) {
            log.error("보고서 없음: {}", reportUid);
            return;
        }

        // LLM 호출
        WeeklyReportDto.Response.WeeklyReportResult result = generateWeeklyReport(userUid, chat);

        // 보고서 내용 및 상태 업데이트
        report.setContent(result.getReportContent());
        report.setStatus("COMPLETE");
        weeklyReportRepository.save(report);

        log.debug("LLM 비동기 처리 완료 : {} {}", reportUid, Duration.between(now, LocalDateTime.now()).toMinutesPart());
    }

    /**
     * 주간 리포트 생성
     */
    public WeeklyReportDto.Response.WeeklyReportResult generateWeeklyReport(String userUid, String chat) {
        log.info("주간 리포트 생성 generateWeeklyReport - 사용자ID: {}", userUid);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        // 데이터 수집
        List<ScheduleDto.Response.CalendarEvent> weekEvents =
                scheduleService.getSchedulesByDateRange(userUid, startOfWeek, endOfWeek);

        List<ScheduleDto.Response.CalendarEvent> upcomingEvents =
                scheduleService.getUpcomingEvents(userUid);

        log.info("chat 합친 content 전체 = {}", chat + weekEvents + upcomingEvents);

        String reportContent = ollamaService.generateWeeklyReport(new WeeklyReportDto.Request.WeeklyReportRequest(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), userUid, chat ,weekEvents, upcomingEvents
        ));

        String reportPeriod = startOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + " ~ " + endOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return WeeklyReportDto.Response.WeeklyReportResult.builder()
                .reportDate(now)
                .reportPeriod(reportPeriod)
                .title(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 주간보고")
                .upcomingEvents(upcomingEvents)
                .reportContent(reportContent)
                .build();
    }
}
