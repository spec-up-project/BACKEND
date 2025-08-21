FROM eclipse-temurin:21-jdk-alpine

# curl, bash, vim 설치
RUN apk update && apk add curl bash vim

# JAR 복사
ARG JAR_FILE=build/libs/whirlwind-0.0.1.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]