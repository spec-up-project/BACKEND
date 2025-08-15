package com.neekly_report.whirlwind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neekly_report.whirlwind.dto.CalendarDto;
import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.Todo;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.repository.ScheduleRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExtractionService {

    private final OllamaService ollamaService;
    private final DucklingService ducklingService;
    private final ObjectMapper objectMapper;

    // Repositories
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 텍스트에서 일정/할일 추출
     */
    public ExtractionDto.Response.ExtractionResult extractFromText(
            ExtractionDto.Request.TextExtractionRequest request, String userId) {

        long startTime = System.currentTimeMillis();

        try {
            log.info("텍스트 추출 시작 - 사용자: {}, 텍스트 길이: {}자", userId, request.getText().length());

            // 1. Duckling으로 날짜/시간 정보 추출
            List<DucklingService.DateTimeInfo> dateTimeInfos =
                    ducklingService.extractDateTime(request.getText(), "ko");

            // 2. Ollama로 구조화된 데이터 추출
            String structuredData = ollamaService.extractStructuredData(request.getText());

            // 3. JSON 파싱 및 엔터티 생성
            ParsedExtractionData parsedData = parseStructuredData(structuredData, request.getText());

            // 4. Duckling 정보와 병합
            mergeDucklingTimeInfo(parsedData, dateTimeInfos);

            // 5. 데이터베이스 저장
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

            List<CalendarDto.Response.CalendarEvent> savedEvents =
                    saveSchedules(parsedData.schedules, user, request.getSourceType());

            long processingTime = System.currentTimeMillis() - startTime;

            log.info("텍스트 추출 완료 - 일정: {}개, 처리시간: {}ms",
                    savedEvents.size(), processingTime);

            return ExtractionDto.Response.ExtractionResult.builder()
                    .events(savedEvents)
                    .originalText(request.getText())
                    .processedText(structuredData)
                    .sourceType(request.getSourceType())
                    .processingTimeMs(processingTime)
                    .success(true)
                    .savedEventsCount(savedEvents.size())
                    .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("텍스트 추출 실패: {}", e.getMessage(), e);

            return ExtractionDto.Response.ExtractionResult.builder()
                    .originalText(request.getText())
                    .sourceType(request.getSourceType())
                    .processingTimeMs(processingTime)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * 이메일에서 일정/할일 추출
     */
    public ExtractionDto.Response.ExtractionResult extractFromEmail(
            ExtractionDto.Request.EmailExtractionRequest request, String userId) {

        long startTime = System.currentTimeMillis();

        try {
            log.info("이메일 추출 시작 - 사용자: {}, 제목: {}", userId, request.getSubject());

            // 1. 이메일 내용 요약
            String emailContent = "제목: " + request.getSubject() + "\n\n" + request.getBody();
            String summary = ollamaService.summarizeEmail(emailContent);

            // 2. 텍스트 추출 프로세스 재사용
            ExtractionDto.Request.TextExtractionRequest textRequest =
                    ExtractionDto.Request.TextExtractionRequest.builder()
                            .text(summary)
                            .sourceType("EMAIL")
                            .build();

            ExtractionDto.Response.ExtractionResult result = extractFromText(textRequest, userId);

            // 3. 이메일 관련 정보 추가
            result.setOriginalText(emailContent);
            result.setProcessedText(summary);

            return result;

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("이메일 추출 실패: {}", e.getMessage(), e);

            return ExtractionDto.Response.ExtractionResult.builder()
                    .originalText(request.getSubject() + "\n" + request.getBody())
                    .sourceType("EMAIL")
                    .processingTimeMs(processingTime)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * 추출 미리보기 (저장하지 않음)
     */
    public ExtractionDto.Response.ExtractionPreview previewExtraction(
            ExtractionDto.Request.TextExtractionRequest request) {

        long startTime = System.currentTimeMillis();

        try {
            // Ollama로 구조화된 데이터 추출
            String structuredData = ollamaService.extractStructuredData(request.getText());

            // JSON 파싱
            ParsedExtractionData parsedData = parseStructuredData(structuredData, request.getText());

            // Duckling 정보 병합
            List<DucklingService.DateTimeInfo> dateTimeInfos =
                    ducklingService.extractDateTime(request.getText(), "ko");
            mergeDucklingTimeInfo(parsedData, dateTimeInfos);

            // DTO 변환 (저장하지 않고 미리보기용)
            List<CalendarDto.Response.CalendarEventPreview> eventPreviews =
                    parsedData.schedules.stream()
                            .map(this::toEventPreview)
                            .toList();

            List<TodoDto.Response.TodoItemPreview> todoPreviews =
                    parsedData.todos.stream()
                            .map(this::toTodoPreview)
                            .toList();

            long processingTime = System.currentTimeMillis() - startTime;

            return ExtractionDto.Response.ExtractionPreview.builder()
                    .events(eventPreviews)
                    .todos(todoPreviews)
                    .originalText(request.getText())
                    .processedText(structuredData)
                    .processingTimeMs(processingTime)
                    .build();

        } catch (Exception e) {
            log.error("추출 미리보기 실패: {}", e.getMessage(), e);

            return ExtractionDto.Response.ExtractionPreview.builder()
                    .originalText(request.getText())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .events(new ArrayList<>())
                    .todos(new ArrayList<>())
                    .build();
        }
    }

    /**
     * 구조화된 데이터 파싱
     */
    private ParsedExtractionData parseStructuredData(String structuredData, String rawText) {
        List<Schedule> schedules = new ArrayList<>();
        List<Todo> todos = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(structuredData);

            // 일정 파싱
            if (rootNode.has("events")) {
                JsonNode eventsNode = rootNode.get("events");
                for (JsonNode eventNode : eventsNode) {
                    Schedule schedule = parseScheduleFromJson(eventNode, rawText);
                    if (schedule != null) {
                        schedules.add(schedule);
                    }
                }
            }

            // 할일 파싱
            if (rootNode.has("todos")) {
                JsonNode todosNode = rootNode.get("todos");
                for (JsonNode todoNode : todosNode) {
                    Todo todo = parseTodoFromJson(todoNode, rawText);
                    if (todo != null) {
                        todos.add(todo);
                    }
                }
            }

        } catch (Exception e) {
            log.warn("JSON 파싱 실패, fallback 처리: {}", e.getMessage());

            // Fallback: 기본 할일로 등록
            Todo fallbackTodo = Todo.builder()
                    .title("텍스트 검토 필요")
                    .description(rawText)
                    .priority("MEDIUM")
                    .status("TODO")
                    .rawText(rawText)
                    .build();
            todos.add(fallbackTodo);
        }

        return new ParsedExtractionData(schedules, todos);
    }

    /**
     * JSON에서 일정 파싱
     */
    private Schedule parseScheduleFromJson(JsonNode eventNode, String rawText) {
        try {
            String title = eventNode.has("title") ? eventNode.get("title").asText() : "새 일정";
            String description = eventNode.has("description") ? eventNode.get("description").asText() : "";

            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            if (eventNode.has("startTime") && !eventNode.get("startTime").isNull()) {
                startTime = parseDateTime(eventNode.get("startTime").asText());
            }

            if (eventNode.has("endTime") && !eventNode.get("endTime").isNull()) {
                endTime = parseDateTime(eventNode.get("endTime").asText());
            }

            // 기본값 설정
            if (startTime == null) {
                startTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0);
            }
            if (endTime == null) {
                endTime = startTime.plusHours(1);
            }

            return Schedule.builder()
                    .title(title)
                    .content(description.isEmpty() ? title : description)
                    .startTime(startTime)
                    .endTime(endTime)
                    .rawText(rawText)
                    .build();

        } catch (Exception e) {
            log.warn("일정 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JSON에서 할일 파싱
     */
    private Todo parseTodoFromJson(JsonNode todoNode, String rawText) {
        try {
            String title = todoNode.has("title") ? todoNode.get("title").asText() : "새 할일";
            String description = todoNode.has("description") ? todoNode.get("description").asText() : "";
            String priority = todoNode.has("priority") ? todoNode.get("priority").asText() : "MEDIUM";
            String status = todoNode.has("status") ? todoNode.get("status").asText() : "TODO";

            LocalDateTime dueDate = null;
            if (todoNode.has("dueDate") && !todoNode.get("dueDate").isNull()) {
                dueDate = parseDateTime(todoNode.get("dueDate").asText());
            }

            return Todo.builder()
                    .title(title)
                    .description(description.isEmpty() ? title : description)
                    .priority(priority)
                    .status(status)
                    .dueDate(dueDate)
                    .rawText(rawText)
                    .build();

        } catch (Exception e) {
            log.warn("할일 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Duckling 시간 정보와 병합
     */
    private void mergeDucklingTimeInfo(ParsedExtractionData parsedData, List<DucklingService.DateTimeInfo> dateTimeInfos) {
        if (dateTimeInfos.isEmpty()) return;

        // 첫 번째 시간 정보를 첫 번째 일정에 적용
        if (!parsedData.schedules.isEmpty()) {
            Schedule firstSchedule = parsedData.schedules.get(0);
            DucklingService.DateTimeInfo firstTimeInfo = dateTimeInfos.get(0);

            firstSchedule.setStartTime(firstTimeInfo.getStart());
            firstSchedule.setEndTime(firstTimeInfo.getEnd());
        }
    }

    /**
     * 일정 저장
     */
    private List<CalendarDto.Response.CalendarEvent> saveSchedules(List<Schedule> schedules, User user, String source) {
        List<CalendarDto.Response.CalendarEvent> result = new ArrayList<>();

        for (Schedule schedule : schedules) {
            schedule.setUser(user);
            schedule.setSource(source);
            Schedule saved = scheduleRepository.save(schedule);
            result.add(toCalendarEvent(saved));
        }

        return result;
    }

    /**
     * 날짜/시간 문자열 파싱
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            log.warn("날짜/시간 파싱 실패: {}", dateTimeStr);
            return LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0);
        }
    }

    // DTO 변환 메서드들
    private CalendarDto.Response.CalendarEvent toCalendarEvent(Schedule schedule) {
        return CalendarDto.Response.CalendarEvent.builder()
                .scheduleId(schedule.getScheduleUid())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .source(schedule.getSource())
                .rawText(schedule.getRawText())
                .createDate(schedule.getCreateDate())
                .modifyDate(schedule.getModifyDate())
                .build();
    }

    private CalendarDto.Response.CalendarEventPreview toEventPreview(Schedule schedule) {
        return CalendarDto.Response.CalendarEventPreview.builder()
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .rawText(schedule.getRawText())
                .build();
    }

    private TodoDto.Response.TodoItemPreview toTodoPreview(Todo todo) {
        return TodoDto.Response.TodoItemPreview.builder()
                .title(todo.getTitle())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .priority(todo.getPriority())
                .rawText(todo.getRawText())
                .build();
    }

    public List<ScheduleDto.Request.ScheduleCreateRequest> extractSchedulesFromText(String text, String source) {
        List<ScheduleDto.Request.ScheduleCreateRequest> schedules = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                List<DucklingService.DateTimeInfo> dateTimeInfos = ducklingService.extractDateTime(line, "ko");
                if (!dateTimeInfos.isEmpty()) {
                    DucklingService.DateTimeInfo info = dateTimeInfos.get(0);
                    String title = line.replace(info.getText(), "").trim();
                    if (title.isEmpty()) title = "새 일정";

                    ScheduleDto.Request.ScheduleCreateRequest schedule = ScheduleDto.Request.ScheduleCreateRequest.builder()
                            .title(title)
                            .content("자동 생성된 일정: " + title)
                            .startTime(info.getStart())
                            .endTime(info.getEnd())
                            .rawText(line)
                            .source(source)
                            .build();

                    schedules.add(schedule);
                }
            } catch (Exception e) {
                log.warn("일정 추출 중 오류 발생: {}", e.getMessage());
            }
        }
        return schedules;
    }


    /**
     * 파싱된 추출 데이터 내부 클래스
     */
    private static class ParsedExtractionData {
        final List<Schedule> schedules;
        final List<Todo> todos;

        ParsedExtractionData(List<Schedule> schedules, List<Todo> todos) {
            this.schedules = schedules;
            this.todos = todos;
        }
    }
}