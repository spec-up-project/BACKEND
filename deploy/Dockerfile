FROM eclipse-temurin:21-jdk-alpine

# JAR 복사
ARG JAR_FILE=build/libs/whirlwind-0.0.1.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]