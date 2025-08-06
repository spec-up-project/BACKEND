package com.neekly_report.whirlwind.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 자연어 텍스트에서 일정 정보를 추출하는 서비스
 * 오픈소스 AI 라이브러리인 Duckling을 활용하여 자연어 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NLPCalendarService {

    private final DucklingService ducklingService;
    
    // 제목 추출을 위한 패턴 (날짜/시간 관련 텍스트를 제거하기 위함)
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile(
            "(\\d{4}[-./]\\d{1,2}[-./]\\d{1,2})|" +  // 2025-08-06, 2025/08/06, 2025.08.06
            "(\\d{1,2}월\\s*\\d{1,2}일)|" +          // 8월 6일
            "(오늘|내일|모레|어제|그제)|" +           // 오늘, 내일, 모레, 어제, 그제
            "(\\d{1,2}[:.시]\\d{0,2}\\s*(분|초)?)|" + // 14:30, 14시30분
            "((오전|오후)\\s*\\d{1,2}[:.시]\\s*\\d{0,2}\\s*(분|초)?)|" + // 오후 2시 30분, 오전 10시
            "(\\d+)\\s*(시간|분|초)(?:\\s*(\\d+)\\s*(분|초)?)" // 1시간, 30분, 2시간 30분
    );

    /**
     * 자연어 텍스트에서 일정 정보를 추출하여 ScheduleDTO 리스트로 반환
     * 
     * @param text 자연어 텍스트
     * @param source 입력 소스 (TEXT, FILE 등)
     * @return 추출된 일정 정보 리스트
     */
    public List<ScheduleDTO.Request.ScheduleCreateRequest> extractSchedulesFromText(String text, String source) {
        List<ScheduleDTO.Request.ScheduleCreateRequest> schedules = new ArrayList<>();
        
        // 텍스트를 줄 단위로 분리하여 각 줄에서 일정 정보 추출
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            try {
                ScheduleDTO.Request.ScheduleCreateRequest schedule = extractScheduleFromLine(line);
                if (schedule != null) {
                    schedule.setRawText(line);
                    schedule.setSource(source);
                    schedules.add(schedule);
                }
            } catch (Exception e) {
                log.warn("일정 추출 중 오류 발생: {}", e.getMessage());
            }
        }
        
        return schedules;
    }
    
    /**
     * 한 줄의 텍스트에서 일정 정보 추출
     * Duckling AI를 사용하여 날짜/시간 정보 추출
     * 
     * @param line 텍스트 한 줄
     * @return 추출된 일정 정보
     */
    private ScheduleDTO.Request.ScheduleCreateRequest extractScheduleFromLine(String line) {
        // Duckling을 사용하여 날짜/시간 정보 추출
        List<DucklingService.DateTimeInfo> dateTimeInfos = ducklingService.extractDateTime(line, "ko");
        
        if (dateTimeInfos.isEmpty()) {
            // 날짜/시간 정보가 없으면 현재 시간 기준으로 1시간 일정 생성
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            LocalDateTime end = now.plusHours(1);
            
            return createSchedule(line, now, end);
        }
        
        // 첫 번째 날짜/시간 정보 사용
        DucklingService.DateTimeInfo info = dateTimeInfos.get(0);
        
        return createSchedule(line, info.getStart(), info.getEnd());
    }
    
    /**
     * 추출된 정보로 일정 DTO 생성
     * 
     * @param text 원본 텍스트
     * @param start 시작 시간
     * @param end 종료 시간
     * @return 일정 DTO
     */
    private ScheduleDTO.Request.ScheduleCreateRequest createSchedule(String text, LocalDateTime start, LocalDateTime end) {
        // 제목 추출 (날짜/시간 관련 텍스트 제거)
        String title = extractTitle(text);
        
        return ScheduleDTO.Request.ScheduleCreateRequest.builder()
                .title(title)
                .content("자동 생성된 일정: " + title)
                .startTime(start)
                .endTime(end)
                .build();
    }
    
    /**
     * 텍스트에서 제목 추출 (날짜, 시간, 기간을 제외한 텍스트)
     * 
     * @param text 텍스트
     * @return 추출된 제목
     */
    private String extractTitle(String text) {
        // 날짜/시간 패턴 제거
        String title = DATE_TIME_PATTERN.matcher(text).replaceAll("");
        
        // 불필요한 공백 제거 및 정리
        title = title.replaceAll("\\s+", " ").trim();
        
        // 제목이 비어있으면 기본값 설정
        if (title.isEmpty()) {
            title = "새 일정";
        }
        
        return title;
    }
}