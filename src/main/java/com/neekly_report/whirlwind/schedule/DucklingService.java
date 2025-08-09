package com.neekly_report.whirlwind.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Duckling NLP 서비스
 * Duckling은 Facebook에서 개발한 오픈소스 NLP 라이브러리로,
 * 날짜, 시간, 숫자, 기간 등의 구조화된 데이터를 자연어 텍스트에서 추출하는 데 특화되어 있습니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DucklingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Duckling 서버 URL (로컬 또는 원격 서버)
    @Value("${app.duckling.url}")
    private String DUCKLING_URL;
    
    /**
     * 텍스트에서 날짜/시간 정보 추출
     * 
     * @param text 분석할 텍스트
     * @param locale 언어 설정 (ko, en 등)
     * @return 추출된 날짜/시간 정보 목록
     */
    public List<DateTimeInfo> extractDateTime(String text, String locale) {
        List<DateTimeInfo> results = new ArrayList<>();
        try {
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("text", text);
            requestBody.add("locale", locale);
            requestBody.add("dims", "[\"time\"]");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(DUCKLING_URL, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            for (JsonNode node : root) {
                if (node.has("dim") && "time".equals(node.get("dim").asText())) {
                    JsonNode value = node.get("value");
                    if (value.has("type")) {
                        String type = value.get("type").asText();
                        if ("value".equals(type) && value.has("value")) {
                            String dateTimeStr = value.get("value").asText();
                            LocalDateTime dateTime = parseDateTime(dateTimeStr);
                            DateTimeInfo info = new DateTimeInfo();
                            info.setType("DATETIME");
                            info.setStart(dateTime);
                            info.setEnd(dateTime.plusHours(1));
                            info.setText(node.get("body").asText());
                            results.add(info);
                        } else if ("interval".equals(type) && value.has("from") && value.has("to")) {
                            String fromStr = value.get("from").get("value").asText();
                            String toStr = value.get("to").get("value").asText();
                            LocalDateTime from = parseDateTime(fromStr);
                            LocalDateTime to = parseDateTime(toStr);
                            DateTimeInfo info = new DateTimeInfo();
                            info.setType("INTERVAL");
                            info.setStart(from);
                            info.setEnd(to);
                            info.setText(node.get("body").asText());
                            results.add(info);
                        }
                    }
                }
            }
            return results;
        } catch (Exception e) {
            log.error("Duckling API 호출 중 오류 발생: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Duckling 날짜/시간 문자열을 LocalDateTime으로 변환
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // Duckling은 ISO 형식의 날짜/시간을 반환 (2025-08-06T14:00:00.000Z)
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr.replace("Z", ""), formatter);
        } catch (Exception e) {
            log.warn("날짜/시간 파싱 오류: {}", e.getMessage());
            return LocalDateTime.now();
        }
    }
    
    /**
     * 날짜/시간 정보를 담는 내부 클래스
     */
    @Getter
    public static class DateTimeInfo {
        private String type; // DATETIME, INTERVAL
        private LocalDateTime start;
        private LocalDateTime end;
        private String text; // 원본 텍스트

        public void setType(String type) {
            this.type = type;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }

        public void setText(String text) {
            this.text = text;
        }
        
        public LocalDate getDate() {
            return start != null ? start.toLocalDate() : null;
        }
        
        public LocalTime getTime() {
            return start != null ? start.toLocalTime() : null;
        }
        
        public int getDurationMinutes() {
            if (start != null && end != null) {
                return (int) java.time.Duration.between(start, end).toMinutes();
            }
            return 60; // 기본 1시간
        }
    }
}