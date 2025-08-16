package com.neekly_report.whirlwind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neekly_report.whirlwind.dto.ExtractionDto;
import com.neekly_report.whirlwind.dto.ScheduleDto;
import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.Todo;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.exception.NoDateTimeFormatException;
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

    /*
        사용자 입력 텍스트
           ↓
        DucklingService.extractDateTime → 시간 정보 추출
           ↓
        createScheduleCandidatesFromDuckling → Schedule 후보 생성
           ↓
        enrichScheduleWithOllama → 제목/내용 보완
           ↓
        List<Schedule> 완성
           ↓
        ParsedExtractionData → 일정 리스트 + 원본 JSON
           ↓
        ExtractionResult → CalendarEvent 리스트로 변환 후 반환
     */

    /**
     * 텍스트에서 일정/할일 추출
     */
    public ExtractionDto.Response.ExtractionResult extractDatetimeFromText(String chat, String userId) {
        long startTime = System.currentTimeMillis();
        try {
            // 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

            // Duckling 기반 시간 추출
            List<DucklingService.DateTimeInfo> dateTimes = ducklingService.extractDateTime(chat, "ko");
            List<Schedule> schedules;

            if (!dateTimes.isEmpty()) {
                // Duckling 기반 일정 후보 생성
                schedules = createScheduleCandidatesFromDuckling(chat, dateTimes);

                // Ollama로 제목/내용 보완
                for (Schedule schedule : schedules) {
                    enrichScheduleWithOllama(schedule);
                    schedule.setUser(user);
                    schedule.setSource("TEXT");
                }
            } else {
                // Duckling 실패 시 Ollama 단독 추출 fallback
                String structuredData = ollamaService.extractStructuredScheduleData(chat, LocalDateTime.now().toString());
                ExtractionDto.Response.ParsedExtractionData parsedData = parseStructuredData(structuredData, chat);
                schedules = parsedData.getSchedules();
                for (Schedule schedule : schedules) {
                    schedule.setUser(user);
                    schedule.setSource("TEXT");
                }
            }

            // 일정 저장
            List<ScheduleDto.Response.CalendarEvent> savedEvents = saveSchedules(schedules, user);

            long processingTime = System.currentTimeMillis() - startTime;
            return ExtractionDto.Response.ExtractionResult.builder()
                    .schedules(savedEvents)
                    .originalText(chat)
                    .processedText("DUCKLING+OLLAMA")
                    .sourceType("TEXT")
                    .processingTimeMs(processingTime)
                    .success(true)
                    .savedEventsCount(savedEvents.size())
                    .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("텍스트 추출 실패: {}", e.getMessage(), e);
            return ExtractionDto.Response.ExtractionResult.builder()
                    .originalText(chat)
                    .sourceType("TEXT")
                    .processingTimeMs(processingTime)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }


    public ExtractionDto.Response.ParsedExtractionData extractScheduleJson(String chat, String userId) {
        log.info("텍스트 추출 시작 - 사용자: {}, 텍스트 길이: {}자", userId, chat.length());

        // 1. Ollama로 구조화된 데이터 추출
        String structuredData;
        if (ducklingService.hasValidDateTime(chat, "ko")) {
            // Duckling 결과를 기반으로 간단한 일정 JSON 생성 (임시)
            structuredData = ducklingToSimpleJson(chat, ducklingService.extractDateTime(chat, "ko"));
        } else {
            throw new NoDateTimeFormatException("유효한 일시를 포함해주세요.");
        }
        // 2. JSON 파싱 및 엔터티 생성
        return parseStructuredData(structuredData, chat);
    }

    private String ducklingToSimpleJson(String rawText, List<DucklingService.DateTimeInfo> dateTimes) {
        if (dateTimes.isEmpty()) return "{}";
        DucklingService.DateTimeInfo dt = dateTimes.getFirst(); // 첫 번째만 사용 (단계적 고도화)
        return """
                {
                  "schedules": [
                    {
                      "title": "추출된 일정",
                      "content": "",
                      "startTime": "%s",
                      "endTime": "%s",
                      "rawText": "%s",
                      "source": "DUCKLING"
                    }
                  ]
                }
                """.formatted(dt.getStart(), dt.getEnd(), rawText);
    }

    private List<Schedule> createScheduleCandidatesFromDuckling(String rawText, List<DucklingService.DateTimeInfo> dateTimes) {
        List<Schedule> schedules = new ArrayList<>();
        for (DucklingService.DateTimeInfo dt : dateTimes) {
            Schedule schedule = Schedule.builder()
                    .title("시간 기반 일정")
                    .content("")
                    .startTime(dt.getStart())
                    .endTime(dt.getEnd())
                    .rawText(rawText)
                    .source("DUCKLING")
                    .build();
            schedules.add(schedule);
        }
        return schedules;
    }

    private void enrichScheduleWithOllama(Schedule schedule) {
        String prompt = """
    다음 일정 후보에 대해 제목과 내용을 보완해주세요.
    입력:
    - 시작: %s
    - 종료: %s
    - 원문: %s

    출력 형식:
    {
      "title": "회의",
      "content": "팀 회의 진행"
    }
    """.formatted(
                schedule.getStartTime().toString(),
                schedule.getEndTime().toString(),
                schedule.getRawText()
        );

        try {
            String response = ollamaService.generateResponse(prompt);
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("title")) {
                schedule.setTitle(jsonNode.get("title").asText());
            }
            if (jsonNode.has("content")) {
                schedule.setContent(jsonNode.get("content").asText());
            }
        } catch (Exception e) {
            log.warn("Ollama 일정 보완 실패: {}", e.getMessage());
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

            ExtractionDto.Response.ExtractionResult result = extractDatetimeFromText(textRequest.getText(), userId);

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
            String structuredData = ollamaService.extractStructuredScheduleData(request.getText(), LocalDateTime.now().toString());

            // JSON 파싱
            ExtractionDto.Response.ParsedExtractionData parsedData = parseStructuredData(structuredData, request.getText());

            // DTO 변환 (저장하지 않고 미리보기용)
            List<ScheduleDto.Response.ScheduleEvent> eventPreviews =
                    parsedData.getSchedules().stream()
                            .map(Schedule::toEventPreview)
                            .toList();

            long processingTime = System.currentTimeMillis() - startTime;

            return ExtractionDto.Response.ExtractionPreview.builder()
                    .events(eventPreviews)
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

    private ExtractionDto.Response.ParsedExtractionData parseStructuredData(String structuredData, String rawText) {
        List<Schedule> schedules = new ArrayList<>();

        try {
            // 백틱 제거 및 JSON 블록 추출
            String cleanedJson = extractJsonBlock(structuredData);

            JsonNode rootNode = objectMapper.readTree(cleanedJson);

            if (rootNode.has("schedules") && !rootNode.get("schedules").isNull()) {
                JsonNode scheduleNode = rootNode.get("schedules");
                for (JsonNode eventNode : scheduleNode) {
                    Schedule schedule = parseScheduleFromJson(eventNode, rawText);
                    if (schedule != null) {
                        schedules.add(schedule);
                    }
                }
            }

        } catch (Exception e) {
            log.warn("JSON 파싱 실패, fallback 처리: {}", e.getMessage());
        }

        return new ExtractionDto.Response.ParsedExtractionData(schedules, structuredData);
    }

    private Schedule parseScheduleFromJson(JsonNode node, String rawText) {
        try {
            String title = node.has("title") ? node.get("title").asText() : "제목 없음";
            String content = node.has("content") ? node.get("content").asText() : "";
            LocalDateTime startTime = node.has("startTime") ? LocalDateTime.parse(node.get("startTime").asText()) : null;
            LocalDateTime endTime = node.has("endTime") ? LocalDateTime.parse(node.get("endTime").asText()) : null;
            String source = node.has("source") ? node.get("source").asText() : "TEXT";

            return Schedule.builder()
                    .title(title)
                    .content(content)
                    .startTime(startTime)
                    .endTime(endTime)
                    .rawText(rawText)
                    .source(source)
                    .build();

        } catch (Exception e) {
            log.warn("일정 파싱 실패: {}", e.getMessage());
            return null;
        }
    }


    private String extractJsonBlock(String response) {
        // 백틱 제거 및 JSON 블록만 추출
        if (response.startsWith("```json")) {
            response = response.replaceFirst("```json", "").trim();
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.lastIndexOf("```")).trim();
        }
        return response;
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
     * 일정 저장
     */
    private List<ScheduleDto.Response.CalendarEvent> saveSchedules(List<Schedule> schedules, User user) {
        List<ScheduleDto.Response.CalendarEvent> result = new ArrayList<>();
        log.info("call save schedules: {}, {}, {}", schedules.size(), user.getUserUid(), "TEXT");

        for (Schedule schedule : schedules) {
            schedule.setUser(user);
            schedule.setSource("TEXT");
            Schedule saved = scheduleRepository.save(schedule);
            log.info("saved schedule: {}, {}, {}", saved.getScheduleUid(), user.getUserUid(), "TEXT");
            result.add(saved.toCalendarEvent());
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

    private TodoDto.Response.TodoItemPreview toTodoPreview(Todo todo) {
        return TodoDto.Response.TodoItemPreview.builder()
                .title(todo.getTitle())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .priority(todo.getPriority())
                .rawText(todo.getRawText())
                .build();
    }


}