package com.neekly_report.whirlwind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neekly_report.whirlwind.dto.OllamaDto.OllamaRequest;
import com.neekly_report.whirlwind.dto.OllamaDto.OllamaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${app.ollama.url}")
    private String ollamaUrl;

    @Value("${app.ollama.text-model}")
    private String textModel;

    public String getOllamaResponse(String prompt, String modelCtg) {
        // 요청 객체 생성
        OllamaRequest request = setOllamaModel(modelCtg);
        request.setPrompt(prompt);
        request.setStream(false);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 엔티티 생성
        HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

        // POST 요청
        ResponseEntity<OllamaResponse> response = restTemplate.exchange(
                ollamaUrl,
                HttpMethod.POST,
                entity,
                OllamaResponse.class
        );

        // 응답 반환
        assert response.getBody() != null;
        return response.getBody().getResponse();
    }

    private OllamaRequest setOllamaModel(String modelCtg) {
        OllamaRequest ollamaRequest = new OllamaRequest();
        ollamaRequest.setModel(textModel);

        return ollamaRequest;
    }

    public String generateResponse(String prompt) {
        try {
            String response = webClient.post()
                    .uri(ollamaUrl)
                    .body(Mono.just(Map.of(
                            "model", textModel,
                            "prompt", prompt,
                            "stream", false,
                            "options", Map.of(
                                    "temperature", 0.3,
                                    "top_k", 40,
                                    "top_p", 0.9
                            )
                    )), Map.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return jsonNode.get("response").asText();
            }
        } catch (Exception e) {
            log.error("Ollama 응답 생성 실패: {}", e.getMessage());
            // Fallback: 기본 응답 반환
            return getFallbackResponse(prompt);
        }

        return "";
    }

    /**
     * 텍스트에서 일정과 할일을 구조화된 데이터로 추출
     */
    public String extractStructuredData(String text) {
        String prompt = """
            다음 텍스트에서 일정(이벤트)를 추출하여 JSON 형태로 반환해주세요.
            
            규칙:
            1. 날짜와 시간이 명시된 것은 일정(events)로 분류
            2. 우선순위는 텍스트의 긴급성을 고려해 LOW, MEDIUM, HIGH, URGENT 중 하나로 설정
            3. 날짜/시간 정보가 불명확하면 현재 시점 기준으로 합리적 추정
            4. 응답은 반드시 valid JSON 형식이어야 함
            
            형식:
            {
              "events": [
                {
                  "title": "이벤트 제목",
                  "description": "상세 설명",
                  "startTime": "2024-01-01T10:00:00",
                  "endTime": "2024-01-01T11:00:00",
                  "isAllDay": false,
                  "priority": "MEDIUM"
                }
              ]
            }
            
            텍스트: %s
            
            JSON만 응답해주세요:
            """.formatted(text);

        return generateResponse(prompt);
    }

    /**
     * 이메일 본문 요약
     */
    public String summarizeEmail(String emailContent) {
        String prompt = """
            다음 이메일 내용을 핵심 요점 위주로 3-5줄로 요약해주세요.
            중요한 일정이나 할일이 있다면 별도로 언급해주세요.
            
            이메일 내용: %s
            
            요약:
            """.formatted(emailContent);

        return generateResponse(prompt);
    }

    /**
     * OCR로 추출된 텍스트 정제 및 요약
     */
    public String cleanAndSummarizeOcrText(String ocrText) {
        String prompt = """
            다음은 OCR로 추출된 텍스트입니다. 오타나 인식 오류를 수정하고
            의미 있는 내용만 정리해서 요약해주세요.
            
            OCR 텍스트: %s
            
            정제된 요약:
            """.formatted(ocrText);

        return generateResponse(prompt);
    }

    /**
     * 주간 리포트 생성
     */
    public String generateWeeklyReport(String scheduleData, String completionStats) {
        String prompt = """
            다음 데이터를 바탕으로 주간 리포트를 Markdown 형식으로 생성해주세요.
            
            일정 데이터: %s
            완료 통계: %s
            
            # 주간 리포트 (YYYY-MM-DD ~ YYYY-MM-DD)
            
            ## 📅 이번 주 주요 일정
            
            ## ✅ 완료된 할일
            
            ## 📋 진행중인 할일
            
            ## 📊 이번 주 성과
            
            ## 💡 다음 주 계획
            
            형식으로 작성해주세요.
            """.formatted(scheduleData, completionStats);

        return generateResponse(prompt);
    }

    /**
     * Ollama 서버 장애 시 대체 응답
     */
    private String getFallbackResponse(String prompt) {
        if (prompt.contains("JSON")) {
            return """
                {
                  "events": [],
                  "todos": [{
                    "title": "텍스트 검토 필요",
                    "description": "AI 서버 연결 실패로 수동 확인 필요",
                    "dueDate": null,
                    "priority": "MEDIUM",
                    "status": "PENDING"
                  }]
                }
                """;
        }
        return "AI 서버 연결 실패로 자동 처리할 수 없습니다. 수동으로 확인해 주세요.";
    }
}