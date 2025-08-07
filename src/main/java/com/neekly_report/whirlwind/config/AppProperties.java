package com.neekly_report.whirlwind.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private Ollama ollama = new Ollama();
    private Duckling duckling = new Duckling();
    private Tesseract tesseract = new Tesseract();

    @Data
    public static class Ollama {
        private String url = "http://localhost:11434";
        private String model = "phi3";
        private int timeoutSeconds = 30;
    }

    @Data
    public static class Duckling {
        private String url = "http://localhost:8001";
        private int timeoutSeconds = 10;
    }

    @Data
    public static class Tesseract {
        private String dataPath = "/usr/share/tesseract-ocr/5/tessdata";
        private int timeoutSeconds = 30;
        private String defaultLanguage = "kor+eng";
    }
}
