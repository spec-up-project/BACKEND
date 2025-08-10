FROM eclipse-temurin:21-jdk-alpine

# Tesseract 설치 (Alpine용)
#RUN apk update && \
#    apk add --no-cache tesseract-ocr tesseract-ocr-data-eng tesseract-ocr-data-kor

# JAR 복사
ARG JAR_FILE=../build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]