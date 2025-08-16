package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.CalendarDto;
import com.neekly_report.whirlwind.dto.CalendarDto.Response.CalendarEvent;
import com.neekly_report.whirlwind.dto.ReportDto;
import com.neekly_report.whirlwind.dto.ReportDto.Response.WeeklySummary;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import com.neekly_report.whirlwind.mapper.ReportMapper;
import com.neekly_report.whirlwind.repository.ReportRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportMapper reportMapper;

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    private final CalendarService calendarService;
    private final OllamaService ollamaService;

    private final ExcelReportGenerator excelReportGenerator;

    public ReportDto.Response.TextReport makeReport(ReportDto.Request.TextReport textReport, String userUid) {
        String reportResult = ollamaService.makeReport(textReport.getContent());
        log.info("structured data: {}", reportResult);

        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        textReport.setContent(reportResult);

        // 리포트 저장
        WeeklyReport report = reportMapper.toEntity(textReport);
        report.setUser(user);
        reportRepository.save(report);

        return reportMapper.toResponse(report);
    }

    /**
     * 주간 리포트 생성
     */
    public ReportDto.Response.WeeklyReport generateWeeklyReport(String userId) {
        log.info("주간 리포트 생성 - 사용자ID: {}", userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        // 데이터 수집
        List<CalendarDto.Response.CalendarEvent> weekEvents =
                calendarService.getEventsByDateRange(userId, startOfWeek, endOfWeek);

        List<CalendarDto.Response.CalendarEvent> upcomingEvents =
                calendarService.getUpcomingEvents(userId);

        // 통계 계산
        ReportDto.Response.WeeklySummary summary = calculateWeeklySummary(
                weekEvents);

        // AI 마크다운 리포트 생성
        String markdownContent = generateMarkdownReport(userId, weekEvents, summary);

        String reportPeriod = startOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + " ~ " + endOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return ReportDto.Response.WeeklyReport.builder()
                .reportDate(now)
                .reportPeriod(reportPeriod)
                .summary(summary)
                .upcomingEvents(upcomingEvents)
                .markdownContent(markdownContent)
                .build();
    }

    /**
     * 월간 리포트 생성
     */
    public ReportDto.Response.MonthlyReport generateMonthlyReport(String userId) {
        log.info("월간 리포트 생성 - 사용자ID: {}", userId);

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        // 주별 요약 생성
        List<ReportDto.Response.WeeklySummary> weeklySummaries = generateWeeklySummariesForMonth(userId, startOfMonth, endOfMonth);

        // 월간 통계 계산
        ReportDto.Response.MonthlySummary monthlySummary = calculateMonthlySummary(weeklySummaries);

        // AI 인사이트 생성
        String insights = generateMonthlyInsights(userId, weeklySummaries, monthlySummary);
        String recommendations = generateMonthlyRecommendations(userId, weeklySummaries, monthlySummary);

        String reportPeriod = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월"));

        return ReportDto.Response.MonthlyReport.builder()
                .reportDate(LocalDateTime.now())
                .reportPeriod(reportPeriod)
                .weeklySummaries(weeklySummaries)
                .summary(monthlySummary)
                .insights(insights)
                .recommendations(recommendations)
                .build();
    }

    private ReportDto.Response.WeeklySummary calculateWeeklySummary(
            List<CalendarDto.Response.CalendarEvent> events) {

        int totalEvents = events.size();

        return ReportDto.Response.WeeklySummary.builder()
                .totalEvents(totalEvents)
                .build();
    }

    private int calculateProductivityScore(double completionRate, int overdue, int urgent, int events) {
        int baseScore = (int) (completionRate * 0.6); // 완료율 60%
        int eventBonus = Math.min(events * 2, 20); // 일정 관리 보너스 최대 20점
        int overduePenalty = overdue * 5; // 연체 페널티
        int urgentPenalty = urgent * 3; // 긴급 업무 페널티

        return Math.max(0, Math.min(100, baseScore + eventBonus - overduePenalty - urgentPenalty));
    }

    private String generateMarkdownReport(String userId,
                                          List<CalendarDto.Response.CalendarEvent> events,
                                          ReportDto.Response.WeeklySummary summary) {

        StringBuilder scheduleData = new StringBuilder();
        events.forEach(event -> scheduleData.append(String.format("- %s (%s)\n",
                event.getTitle(), event.getStartTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")))));

        String stats = String.format("완료율: %.1f%%, 생산성 점수: %d점",
                summary.getCompletionRate(), summary.getProductivityScore());

        return ollamaService.generateWeeklyReport(scheduleData.toString(), stats);
    }

    private List<ReportDto.Response.WeeklySummary> generateWeeklySummariesForMonth(
            String userId, LocalDate startOfMonth, LocalDate endOfMonth) {

        // 월간 리포트의 주별 요약 로직 (간소화)
        return List.of(); // 실제 구현에서는 주별로 계산
    }

    private ReportDto.Response.MonthlySummary calculateMonthlySummary(
            List<ReportDto.Response.WeeklySummary> weeklySummaries) {

        if (weeklySummaries.isEmpty()) {
            return ReportDto.Response.MonthlySummary.builder()
                    .totalEvents(0)
                    .totalTodos(0)
                    .completedTodos(0)
                    .averageCompletionRate(0.0)
                    .averageProductivityScore(0)
                    .build();
        }

        int totalEvents = weeklySummaries.stream().mapToInt(ReportDto.Response.WeeklySummary::getTotalEvents).sum();
        int totalTodos = weeklySummaries.stream().mapToInt(ReportDto.Response.WeeklySummary::getTotalTodos).sum();
        int completedTodos = weeklySummaries.stream().mapToInt(ReportDto.Response.WeeklySummary::getCompletedTodos).sum();

        double avgCompletionRate = weeklySummaries.stream()
                .mapToDouble(ReportDto.Response.WeeklySummary::getCompletionRate)
                .average()
                .orElse(0.0);

        int avgProductivityScore = (int) weeklySummaries.stream()
                .mapToInt(ReportDto.Response.WeeklySummary::getProductivityScore)
                .average()
                .orElse(0.0);

        return ReportDto.Response.MonthlySummary.builder()
                .totalEvents(totalEvents)
                .totalTodos(totalTodos)
                .completedTodos(completedTodos)
                .averageCompletionRate(avgCompletionRate)
                .averageProductivityScore(avgProductivityScore)
                .build();
    }

    public File generateWeeklyReportExcel(String userId) throws IOException {
        // 기존 로직에서 이벤트와 요약 데이터 수집
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        List<CalendarEvent> weekEvents = calendarService.getEventsByDateRange(userId, startOfWeek, endOfWeek);
        WeeklySummary summary = calculateWeeklySummary(weekEvents);

        // 엑셀 생성
        return excelReportGenerator.generateWeeklyExcel(userId, startOfWeek.toLocalDate(), endOfWeek.toLocalDate(), weekEvents, summary);
    }


    private String generateMonthlyInsights(String userId,
                                           List<ReportDto.Response.WeeklySummary> weeklySummaries,
                                           ReportDto.Response.MonthlySummary monthlySummary) {
        // AI를 이용한 월간 인사이트 생성
        return "이번 달 전반적인 생산성이 향상되었습니다.";
    }

    private String generateMonthlyRecommendations(String userId,
                                                  List<ReportDto.Response.WeeklySummary> weeklySummaries,
                                                  ReportDto.Response.MonthlySummary monthlySummary) {
        // AI를 이용한 월간 추천사항 생성
        return "우선순위가 높은 업무에 더 집중해보세요.";
    }
}
