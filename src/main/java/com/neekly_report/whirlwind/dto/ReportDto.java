package com.neekly_report.whirlwind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDto {

    public static class Response {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class WeeklyReport {
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime reportDate;

            private String reportPeriod; // "2024-08-01 ~ 2024-08-07"

            private WeeklySummary summary;
            private List<CalendarDto.Response.CalendarEvent> upcomingEvents;
            private List<TodoDto.Response.TodoItem> pendingTodos;
            private List<TodoDto.Response.TodoItem> completedTodos;
            private String markdownContent; // 마크다운 형식의 리포트
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class WeeklySummary {
            private int totalEvents;
            private int totalTodos;
            private int completedTodos;
            private int pendingTodos;
            private int overdueCounter;
            private int urgentTasks;
            private double completionRate;
            private int productivityScore; // 1-100점
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MonthlyReport {
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private LocalDateTime reportDate;

            private String reportPeriod; // "2024년 8월"
            private List<WeeklySummary> weeklySummaries;
            private MonthlySummary summary;
            private String insights; // AI가 생성한 인사이트
            private String recommendations; // AI가 생성한 추천사항
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MonthlySummary {
            private int totalEvents;
            private int totalTodos;
            private int completedTodos;
            private double averageCompletionRate;
            private int averageProductivityScore;
            private String mostProductiveWeek;
            private String leastProductiveWeek;
        }
    }
}
