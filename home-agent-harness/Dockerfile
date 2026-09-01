FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :home-agent-harness:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /workspace/home-agent-harness/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
