package com.neekly_report.whirlwind.nlpcalendar;

import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.service.NLPCalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NLPCalendarServiceTest {

    @Autowired
    private NLPCalendarService nlpCalendarService;

    @Test
    @DisplayName("단일 일정 텍스트 추출 테스트")
    void extractSingleScheduleTest() {
        // given
        String text = "내일 오후 2시에 팀 미팅";
        
        // when
        List<ScheduleDto.Request.ScheduleCreateRequest> schedules =
                nlpCalendarService.extractSchedulesFromText(text, "TEXT");
        
        // then
        assertEquals(1, schedules.size());
        
        ScheduleDto.Request.ScheduleCreateRequest schedule = schedules.get(0);
        assertEquals("팀 미팅", schedule.getTitle());
        assertEquals("자동 생성된 일정: 팀 미팅", schedule.getContent());
        assertEquals(text, schedule.getRawText());
        assertEquals("TEXT", schedule.getSource());
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime expectedStartTime = LocalDateTime.of(tomorrow, LocalTime.of(14, 0));
        LocalDateTime expectedEndTime = LocalDateTime.of(tomorrow, LocalTime.of(15, 0));
        
        assertEquals(expectedStartTime, schedule.getStartTime());
        assertEquals(expectedEndTime, schedule.getEndTime());
    }
    
    @Test
    @DisplayName("여러 일정 텍스트 추출 테스트")
    void extractMultipleSchedulesTest() {
        // given
        String text = "내일 오후 2시에 팀 미팅\n8월 10일 오전 10시 프로젝트 회의 2시간";
        
        // when
        List<ScheduleDto.Request.ScheduleCreateRequest> schedules =
                nlpCalendarService.extractSchedulesFromText(text, "TEXT");
        
        // then
        assertEquals(2, schedules.size());
        
        // 첫 번째 일정 확인
        ScheduleDto.Request.ScheduleCreateRequest schedule1 = schedules.get(0);
        assertEquals("팀 미팅", schedule1.getTitle());
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime expectedStartTime1 = LocalDateTime.of(tomorrow, LocalTime.of(14, 0));
        LocalDateTime expectedEndTime1 = LocalDateTime.of(tomorrow, LocalTime.of(15, 0));
        
        assertEquals(expectedStartTime1, schedule1.getStartTime());
        assertEquals(expectedEndTime1, schedule1.getEndTime());
        
        // 두 번째 일정 확인
        ScheduleDto.Request.ScheduleCreateRequest schedule2 = schedules.get(1);
        assertEquals("프로젝트 회의", schedule2.getTitle());
        
        LocalDate august10 = LocalDate.of(LocalDate.now().getYear(), 8, 10);
        LocalDateTime expectedStartTime2 = LocalDateTime.of(august10, LocalTime.of(10, 0));
        LocalDateTime expectedEndTime2 = LocalDateTime.of(august10, LocalTime.of(12, 0));
        
        assertEquals(expectedStartTime2, schedule2.getStartTime());
        assertEquals(expectedEndTime2, schedule2.getEndTime());
    }
    
    @Test
    @DisplayName("다양한 날짜 형식 추출 테스트")
    void extractVariousDateFormatsTest() {
        // given
        String text = "2025-08-06 회의\n" +
                      "2025/08/07 미팅\n" +
                      "2025.08.08 프레젠테이션\n" +
                      "8월 9일 워크샵\n" +
                      "오늘 점심 약속\n" +
                      "내일 저녁 식사\n" +
                      "모레 아침 회의";
        
        // when
        List<ScheduleDto.Request.ScheduleCreateRequest> schedules =
                nlpCalendarService.extractSchedulesFromText(text, "TEXT");
        
        // then
        assertEquals(7, schedules.size());
        
        // 날짜 형식별 확인
        assertEquals(LocalDate.of(2025, 8, 6), schedules.get(0).getStartTime().toLocalDate());
        assertEquals(LocalDate.of(2025, 8, 7), schedules.get(1).getStartTime().toLocalDate());
        assertEquals(LocalDate.of(2025, 8, 8), schedules.get(2).getStartTime().toLocalDate());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 8, 9), schedules.get(3).getStartTime().toLocalDate());
        assertEquals(LocalDate.now(), schedules.get(4).getStartTime().toLocalDate());
        assertEquals(LocalDate.now().plusDays(1), schedules.get(5).getStartTime().toLocalDate());
        assertEquals(LocalDate.now().plusDays(2), schedules.get(6).getStartTime().toLocalDate());
    }
    
    @Test
    @DisplayName("다양한 시간 형식 추출 테스트")
    void extractVariousTimeFormatsTest() {
        // given
        String text = "오늘 14:30 회의\n" +
                      "오늘 오후 2시 미팅\n" +
                      "오늘 오후 2시 30분 프레젠테이션\n" +
                      "오늘 오전 10시 워크샵";
        
        // when
        List<ScheduleDto.Request.ScheduleCreateRequest> schedules =
                nlpCalendarService.extractSchedulesFromText(text, "TEXT");
        
        // then
        assertEquals(4, schedules.size());
        
        // 시간 형식별 확인
        assertEquals(LocalTime.of(14, 30), schedules.get(0).getStartTime().toLocalTime());
        assertEquals(LocalTime.of(14, 0), schedules.get(1).getStartTime().toLocalTime());
        assertEquals(LocalTime.of(14, 30), schedules.get(2).getStartTime().toLocalTime());
        assertEquals(LocalTime.of(10, 0), schedules.get(3).getStartTime().toLocalTime());
    }
    
    @Test
    @DisplayName("기간 추출 테스트")
    void extractDurationTest() {
        // given
        String text = "오늘 14:00 1시간 회의\n" +
                      "오늘 15:00 30분 미팅\n" +
                      "오늘 16:00 2시간 30분 워크샵";
        
        // when
        List<ScheduleDto.Request.ScheduleCreateRequest> schedules =
                nlpCalendarService.extractSchedulesFromText(text, "TEXT");
        
        // then
        assertEquals(3, schedules.size());
        
        // 기간별 확인
        LocalDateTime start1 = schedules.get(0).getStartTime();
        LocalDateTime end1 = schedules.get(0).getEndTime();
        assertEquals(60, end1.getMinute() - start1.getMinute() + (end1.getHour() - start1.getHour()) * 60);
        
        LocalDateTime start2 = schedules.get(1).getStartTime();
        LocalDateTime end2 = schedules.get(1).getEndTime();
        assertEquals(30, end2.getMinute() - start2.getMinute() + (end2.getHour() - start2.getHour()) * 60);
        
        LocalDateTime start3 = schedules.get(2).getStartTime();
        LocalDateTime end3 = schedules.get(2).getEndTime();
        assertEquals(150, end3.getMinute() - start3.getMinute() + (end3.getHour() - start3.getHour()) * 60);
    }
}