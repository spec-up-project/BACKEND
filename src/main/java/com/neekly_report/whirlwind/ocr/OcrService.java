package com.neekly_report.whirlwind.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neekly_report.whirlwind.common.ai.EnhancedTesseractService;
import com.neekly_report.whirlwind.common.ai.OllamaService;
import com.neekly_report.whirlwind.dto.OcrDto;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.schedule.ScheduleService;
import com.neekly_report.whirlwind.todo.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService implements OcrServiceImpl {

    private final ScheduleService scheduleService;
    private final TodoService todoService;
    private final EnhancedTesseractService tesseractService;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;

    public OcrDto.Response.OcrResultResponse processImage(MultipartFile image, String userId) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("이미지 OCR 처리 시작 - 사용자: {}, 파일크기: {}KB",
                    userId, image.getSize() / 1024);

            // 1. OCR 수행 (Tesseract)
            String extractedText = tesseractService.extractText(image, "kor+eng");
            log.info("OCR 추출 완료 - 텍스트 길이: {}자", extractedText.length());

            if (extractedText.trim().isEmpty()) {
                return OcrDto.Response.OcrResultResponse.builder()
                        .extractedText("")
                        .summarizedText("이미지에서 텍스트를 찾을 수 없습니다.")
                        .success(false)
                        .errorMessage("OCR 결과가 비어있습니다.")
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            // 2. AI를 이용한 텍스트 정제 및 구조화
            String cleanText = ollamaService.cleanAndSummarizeOcrText(extractedText);
            String structuredData = ollamaService.extractStructuredData(cleanText);

            // 3. JSON 파싱하여 일정/할일 추출
            List<ScheduleDto.Request.ScheduleCreateRequest> schedules = new ArrayList<>();
            List<TodoDto.Request.TodoCreateRequest> todos = new ArrayList<>();

            try {
                JsonNode rootNode = objectMapper.readTree(structuredData);

                // 일정 추출
                if (rootNode.has("events")) {
                    JsonNode eventsNode = rootNode.get("events");
                    for (JsonNode eventNode : eventsNode) {
                        ScheduleDto.Request.ScheduleCreateRequest schedule = parseScheduleFromJson(eventNode, extractedText);
                        if (schedule != null) {
                            schedules.add(schedule);
                        }
                    }
                }

                // 할일 추출
                if (rootNode.has("todos")) {
                    JsonNode todosNode = rootNode.get("todos");
                    for (JsonNode todoNode : todosNode) {
                        TodoDto.Request.TodoCreateRequest todo = parseTodoFromJson(todoNode, extractedText);
                        if (todo != null) {
                            todos.add(todo);
                        }
                    }
                }

            } catch (Exception e) {
                log.warn("JSON 파싱 실패, fallback 처리: {}", e.getMessage());
                // Fallback: 기본 할일로 등록
                todos.add(TodoDto.Request.TodoCreateRequest.builder()
                        .title("OCR 처리된 텍스트 검토")
                        .description(cleanText)
                        .priority("MEDIUM")
                        .status("TODO")
                        .rawText(extractedText)
                        .source("OCR_IMAGE")
                        .build());
            }

            // 4. 데이터베이스에 저장
            int savedSchedules = 0;
            int saveDtodos = 0;

            for (var scheduleReq : schedules) {
                try {
                    scheduleService.createSchedule(userId, scheduleReq);
                    savedSchedules++;
                } catch (Exception e) {
                    log.error("일정 저장 실패: {}", e.getMessage());
                }
            }

            for (var todoReq : todos) {
                try {
                    todoService.createTodo(userId, todoReq);
                    saveDtodos++;
                } catch (Exception e) {
                    log.error("할일 저장 실패: {}", e.getMessage());
                }
            }

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("OCR 처리 완료 - 일정: {}개, 할일: {}개 저장, 처리시간: {}ms",
                    savedSchedules, saveDtodos, processingTime);

            return OcrDto.Response.OcrResultResponse.builder()
                    .extractedText(extractedText)
                    .summarizedText(cleanText)
                    .success(true)
                    .processingTimeMs(processingTime)
                    .savedSchedulesCount(savedSchedules)
                    .savedTodosCount(saveDtodos)
                    .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("OCR 처리 중 오류 발생: {}", e.getMessage(), e);

            return OcrDto.Response.OcrResultResponse.builder()
                    .extractedText("")
                    .summarizedText("처리 중 오류가 발생했습니다: " + e.getMessage())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .processingTimeMs(processingTime)
                    .build();
        }
    }

    /**
     * JSON에서 일정 정보 파싱
     */
    private ScheduleDto.Request.ScheduleCreateRequest parseScheduleFromJson(JsonNode eventNode, String rawText) {
        try {
            String title = eventNode.has("title") ? eventNode.get("title").asText() : "새 일정";
            String description = eventNode.has("description") ? eventNode.get("description").asText() : "";

            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            if (eventNode.has("startTime") && !eventNode.get("startTime").isNull()) {
                startTime = LocalDateTime.parse(eventNode.get("startTime").asText());
            }

            if (eventNode.has("endTime") && !eventNode.get("endTime").isNull()) {
                endTime = LocalDateTime.parse(eventNode.get("endTime").asText());
            }

            // 시간이 없으면 현재 시간 기준으로 설정
            if (startTime == null) {
                startTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0);
            }
            if (endTime == null) {
                endTime = startTime.plusHours(1);
            }

            return ScheduleDto.Request.ScheduleCreateRequest.builder()
                    .title(title)
                    .content(description.isEmpty() ? title : description)
                    .startTime(startTime)
                    .endTime(endTime)
                    .rawText(rawText)
                    .source("OCR_IMAGE")
                    .build();

        } catch (Exception e) {
            log.warn("일정 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JSON에서 할일 정보 파싱
     */
    private TodoDto.Request.TodoCreateRequest parseTodoFromJson(JsonNode todoNode, String rawText) {
        try {
            String title = todoNode.has("title") ? todoNode.get("title").asText() : "새 할일";
            String description = todoNode.has("description") ? todoNode.get("description").asText() : "";
            String priority = todoNode.has("priority") ? todoNode.get("priority").asText() : "MEDIUM";
            String status = todoNode.has("status") ? todoNode.get("status").asText() : "TODO";

            LocalDateTime dueDate = null;
            if (todoNode.has("dueDate") && !todoNode.get("dueDate").isNull()) {
                try {
                    dueDate = LocalDateTime.parse(todoNode.get("dueDate").asText());
                } catch (Exception e) {
                    // 날짜 파싱 실패 시 내일 자정으로 설정
                    dueDate = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59).withSecond(0);
                }
            }

            return TodoDto.Request.TodoCreateRequest.builder()
                    .title(title)
                    .description(description.isEmpty() ? title : description)
                    .priority(priority)
                    .status(status)
                    .dueDate(dueDate)
                    .rawText(rawText)
                    .source("OCR_IMAGE")
                    .build();

        } catch (Exception e) {
            log.warn("할일 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
