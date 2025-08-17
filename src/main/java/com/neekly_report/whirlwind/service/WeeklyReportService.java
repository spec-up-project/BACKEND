package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.ScheduleDto.Response.CalendarEvent;
import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.WeeklyReportDto;
import com.neekly_report.whirlwind.dto.WeeklyReportDto.Response.WeeklySummary;
import com.neekly_report.whirlwind.entity.Category;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.entity.WeeklyReport;
import com.neekly_report.whirlwind.mapper.WeeklyReportMapper;
import com.neekly_report.whirlwind.repository.CategoryRepository;
import com.neekly_report.whirlwind.repository.WeeklyReportRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyReportService {
    private final WeeklyReportMapper weeklyReportMapper;

    private final UserRepository userRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final CategoryRepository categoryRepository;

    private final ScheduleService scheduleService;
    private final OllamaService ollamaService;
    private final WeeklyReportAsyncService weeklyReportAsyncService;

    private final ExcelReportGenerator excelReportGenerator;

    @Transactional
    public String requestReport(String userUid, WeeklyReportDto.Request.WeeklyReportPreview request) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        WeeklyReport report = new WeeklyReport();
        report.setUser(user);
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            report.setTitle(request.getTitle());
        } else {
            report.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 주간보고");
        }
        report.setRawText(request.getChat());
        report.setStatus("REQUEST");

        WeeklyReport savedReport = weeklyReportRepository.save(report);

        // 트랜잭션 커밋 이후에 비동기 호출
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                weeklyReportAsyncService.generateContentAsync(savedReport.getReportUid(), userUid, request.getChat());
            }
        });

        return savedReport.getReportUid();
    }

    @Transactional(readOnly = true)
    public List<WeeklyReportDto.Response.SaveResponse> getReportsByUser(String userUid) {
        return weeklyReportRepository.findWeeklyReportByUser_UserUid(userUid).stream()
                .map(weeklyReportMapper::toSaveResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WeeklyReportDto.Response.WeeklyReportDetail getReportsByUid(String reportUid) {
        return weeklyReportMapper.toWeeklyReportDetail(weeklyReportRepository.findByReportUid(reportUid));
    }

    public WeeklyReportDto.Response.TextReport makeReport(WeeklyReportDto.Request.TextReport textReport, String userUid) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 리포트 저장
        WeeklyReport report = weeklyReportMapper.toEntity(textReport);
        report.setUser(user);
        report.setStatus("REQUEST");
        report.setRawText(textReport.getContent());
        WeeklyReport savedReport = weeklyReportRepository.save(report);

        // 트랜잭션 커밋 이후에 비동기 호출
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                weeklyReportAsyncService.generateContentAsync(savedReport.getReportUid(), userUid, textReport.getContent());
            }
        });

        return weeklyReportMapper.toTextReport(savedReport);
    }

    /**
     * 주간 리포트 생성
     */
    public WeeklyReportDto.Response.WeeklyReportResult generateMarkdown(String userId) {
        log.info("주간 리포트 생성 - 사용자ID: {}", userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        // 데이터 수집
        List<CalendarEvent> weekEvents =
                scheduleService.getSchedulesByDateRange(userId, startOfWeek, endOfWeek);

        List<CalendarEvent> upcomingEvents =
                scheduleService.getUpcomingEvents(userId);

        // 통계 계산
        WeeklyReportDto.Response.WeeklySummary summary = calculateWeeklySummary(
                weekEvents);

        // AI 마크다운 리포트 생성
        String markdownContent = generateMarkdownReport(userId, weekEvents, summary);

        String reportPeriod = startOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + " ~ " + endOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return WeeklyReportDto.Response.WeeklyReportResult.builder()
                .reportDate(now)
                .reportPeriod(reportPeriod)
                .summary(summary)
                .upcomingEvents(upcomingEvents)
                .reportContent(markdownContent)
                .build();
    }

    /**
     * 월간 리포트 생성
     */
    public WeeklyReportDto.Response.MonthlyReport generateMonthlyReport(String userId) {
        log.info("월간 리포트 생성 - 사용자ID: {}", userId);

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        // 주별 요약 생성
        List<WeeklyReportDto.Response.WeeklySummary> weeklySummaries = generateWeeklySummariesForMonth(userId, startOfMonth, endOfMonth);

        // 월간 통계 계산
        WeeklyReportDto.Response.MonthlySummary monthlySummary = calculateMonthlySummary(weeklySummaries);

        // AI 인사이트 생성
        String insights = generateMonthlyInsights(userId, weeklySummaries, monthlySummary);
        String recommendations = generateMonthlyRecommendations(userId, weeklySummaries, monthlySummary);

        String reportPeriod = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월"));

        return WeeklyReportDto.Response.MonthlyReport.builder()
                .reportDate(LocalDateTime.now())
                .reportPeriod(reportPeriod)
                .weeklySummaries(weeklySummaries)
                .summary(monthlySummary)
                .insights(insights)
                .recommendations(recommendations)
                .build();
    }

    private WeeklyReportDto.Response.WeeklySummary calculateWeeklySummary(
            List<CalendarEvent> events) {

        int totalEvents = events.size();

        return WeeklyReportDto.Response.WeeklySummary.builder()
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
                                          List<CalendarEvent> events,
                                          WeeklyReportDto.Response.WeeklySummary summary) {

        StringBuilder scheduleData = new StringBuilder();
        events.forEach(event -> scheduleData.append(String.format("- %s (%s)\n",
                event.getTitle(), event.getStartTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")))));

        String stats = String.format("완료율: %.1f%%, 생산성 점수: %d점",
                summary.getCompletionRate(), summary.getProductivityScore());

        return ollamaService.generateMarkdownWeeklyReport(scheduleData.toString(), stats);
    }

    private List<WeeklyReportDto.Response.WeeklySummary> generateWeeklySummariesForMonth(
            String userId, LocalDate startOfMonth, LocalDate endOfMonth) {

        // 월간 리포트의 주별 요약 로직 (간소화)
        return List.of(); // 실제 구현에서는 주별로 계산
    }

    private WeeklyReportDto.Response.MonthlySummary calculateMonthlySummary(
            List<WeeklyReportDto.Response.WeeklySummary> weeklySummaries) {

        if (weeklySummaries.isEmpty()) {
            return WeeklyReportDto.Response.MonthlySummary.builder()
                    .totalEvents(0)
                    .totalTodos(0)
                    .completedTodos(0)
                    .averageCompletionRate(0.0)
                    .averageProductivityScore(0)
                    .build();
        }

        int totalEvents = weeklySummaries.stream().mapToInt(WeeklyReportDto.Response.WeeklySummary::getTotalEvents).sum();
        int totalTodos = weeklySummaries.stream().mapToInt(WeeklyReportDto.Response.WeeklySummary::getTotalTodos).sum();
        int completedTodos = weeklySummaries.stream().mapToInt(WeeklyReportDto.Response.WeeklySummary::getCompletedTodos).sum();

        double avgCompletionRate = weeklySummaries.stream()
                .mapToDouble(WeeklyReportDto.Response.WeeklySummary::getCompletionRate)
                .average()
                .orElse(0.0);

        int avgProductivityScore = (int) weeklySummaries.stream()
                .mapToInt(WeeklyReportDto.Response.WeeklySummary::getProductivityScore)
                .average()
                .orElse(0.0);

        return WeeklyReportDto.Response.MonthlySummary.builder()
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

        List<CalendarEvent> weekEvents = scheduleService.getSchedulesByDateRange(userId, startOfWeek, endOfWeek);
        WeeklySummary summary = calculateWeeklySummary(weekEvents);

        // 엑셀 생성
        return excelReportGenerator.generateWeeklyExcel(userId, startOfWeek.toLocalDate(), endOfWeek.toLocalDate(), weekEvents, summary);
    }


    private String generateMonthlyInsights(String userId,
                                           List<WeeklyReportDto.Response.WeeklySummary> weeklySummaries,
                                           WeeklyReportDto.Response.MonthlySummary monthlySummary) {
        // AI를 이용한 월간 인사이트 생성
        return "이번 달 전반적인 생산성이 향상되었습니다.";
    }

    private String generateMonthlyRecommendations(String userId,
                                                  List<WeeklyReportDto.Response.WeeklySummary> weeklySummaries,
                                                  WeeklyReportDto.Response.MonthlySummary monthlySummary) {
        // AI를 이용한 월간 추천사항 생성
        return "우선순위가 높은 업무에 더 집중해보세요.";
    }
}
