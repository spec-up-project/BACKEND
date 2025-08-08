package com.neekly_report.whirlwind.common.ai;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class EnhancedTesseractService {

    @Value("${app.tesseract.data-path}")
    private String tessDataPath;

    @Value("${app.tesseract.timeout}")
    private int timeoutSeconds;

    public String extractText(MultipartFile imageFile, String language) throws TesseractException, IOException {
        ITesseract tesseract = createTesseractInstance(language);

        tesseract.setDatapath(tessDataPath); // OS에 따라 경로 조정
        tesseract.setLanguage("kor");

        // MultipartFile을 임시 파일로 변환
        File tempFile = convertMultipartFileToFile(imageFile);

        try {
            // 이미지 전처리
            BufferedImage processedImage = preprocessImage(ImageIO.read(tempFile));

            String result = tesseract.doOCR(processedImage);
            log.info("OCR 추출 완료. 텍스트 길이: {} 자", result.length());

            return cleanOcrResult(result);
        } finally {
            // 임시 파일 삭제
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Tesseract 인스턴스 생성 및 설정
     */
    private ITesseract createTesseractInstance(String language) {
        ITesseract tesseract = new Tesseract();

        // 기본 설정
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage(language != null ? language : "kor+eng");
        tesseract.setOcrEngineMode(1); // LSTM OCR Engine
        tesseract.setPageSegMode(1); // Automatic page segmentation with OSD

        // 성능 최적화 설정
        tesseract.setTessVariable("tessedit_char_whitelist",
                "가-힣ㄱ-ㅎㅏ-ㅣ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ " +
                        ".,!?():;-@#$%^&*+=[]{}|\\\"'<>/~`₩");

        return tesseract;
    }

    /**
     * 이미지 전처리 (품질 향상)
     */
    private BufferedImage preprocessImage(BufferedImage original) {
        // 그레이스케일 변환
        BufferedImage grayscale = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayscale.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        // 대비 향상
        BufferedImage enhanced = enhanceContrast(grayscale);

        // 해상도가 낮으면 2배 확대
        if (enhanced.getWidth() < 800 || enhanced.getHeight() < 600) {
            enhanced = scaleImage(enhanced, 2.0);
        }

        return enhanced;
    }

    /**
     * 대비 향상
     */
    private BufferedImage enhanceContrast(BufferedImage image) {
        BufferedImage result = new BufferedImage(
                image.getWidth(), image.getHeight(), image.getType());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int gray = (rgb >> 16) & 0xFF;

                // 대비 증가 (0.2는 대비 계수)
                gray = Math.min(255, Math.max(0, (int) (255 * Math.pow(gray / 255.0, 0.8))));

                int newRgb = (gray << 16) | (gray << 8) | gray;
                result.setRGB(x, y, newRgb);
            }
        }

        return result;
    }

    /**
     * 이미지 크기 조정
     */
    private BufferedImage scaleImage(BufferedImage image, double scale) {
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        BufferedImage scaled = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = scaled.createGraphics();

        // 고품질 렌더링 설정
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaled;
    }

    /**
     * OCR 결과 정제
     */
    private String cleanOcrResult(String ocrText) {
        if (ocrText == null) return "";

        return ocrText
                .replaceAll("\\s+", " ")
                .replaceAll("[｜│┃]", "|")
                .replaceAll("[－—―]", "-")
                .replaceAll("'+", "'")         // 연속된 작은따옴표를 하나로
                .replaceAll("\"+", "\"")       // 연속된 큰따옴표를 하나로
                .trim();
    }


    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".png";

        File tempFile = File.createTempFile("ocr_temp_", extension);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }
}
