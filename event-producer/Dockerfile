FROM gradle:8.5-jdk21 AS build

WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN ./gradlew clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/event-producer-0.0.1-SNAPSHOT.jar /app/event-producer.jar
COPY --from=build /app/src/main/resources/ /app/resources/

ENTRYPOINT ["java", "-jar", "/app/event-producer.jar"]
