package com.neekly_report.whirlwind.ocr;

import com.neekly_report.whirlwind.schedule.ScheduleDTO;
import com.neekly_report.whirlwind.schedule.ScheduleService;
import com.neekly_report.whirlwind.todo.TodoDTO;
import com.neekly_report.whirlwind.todo.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrService implements OcrServiceImpl{

    private final ScheduleService scheduleService;
    private final TodoService todoService;

    public OcrDTO.Response.OcrResultResponse processImage(MultipartFile image, String userId) {
        // 1. OCR 수행 (Tesseract, Google Vision 등)
        String extractedText = performOcr(image);

        // 2. 요약
        String summary = summarizeText(extractedText);

        // 3. 일정 및 할일 추출
        List<ScheduleDTO.Request.ScheduleCreateRequest> schedules = extractSchedules(summary);
        List<TodoDTO.Request.TodoCreateRequest> todos = extractTodos(summary);

        // 4. 저장
        for (var req : schedules) {
            scheduleService.createSchedule(userId, req);
        }

        for (var req : todos) {
            todoService.createTodo(userId, req);
        }

        return OcrDTO.Response.OcrResultResponse.builder()
                .extractedText(extractedText)
                .summarizedText(summary)
                .build();
    }

    // --- OCR 처리 예시 ---
    private String performOcr(MultipartFile file) {
        // 예: Tesseract OCR 사용 (Java wrapper) 또는 외부 Python OCR 마이크로서비스 호출
        return "회의는 8월 10일 오후 2시에 시작합니다. 할일: 보고서 작성, 회의록 정리";
    }

    private String summarizeText(String text) {
        // 간단한 rule 기반 요약 or 외부 요약기 연동
        return text; // 요약 없이 원문 사용 가능
    }

    private List<ScheduleDTO.Request.ScheduleCreateRequest> extractSchedules(String text) {
        // TODO: NLP 처리 or rule 기반
        return List.of(ScheduleDTO.Request.ScheduleCreateRequest.builder()
                .title("회의")
                .startTime(LocalDateTime.of(2025, 8, 10, 14, 0))
                .endTime(LocalDateTime.of(2025, 8, 10, 15, 0))
                .rawText(text)
                .build());
    }

    private List<TodoDTO.Request.TodoCreateRequest> extractTodos(String text) {
        return List.of(
                TodoDTO.Request.TodoCreateRequest.builder()
                        .title("보고서 작성")
                        .dueDate(LocalDateTime.of(2025, 8, 11, 23, 59))
                        .priority("HIGH")
                        .rawText(text)
                        .build()
        );
    }
}
