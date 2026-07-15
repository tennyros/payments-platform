FROM gradle:9.0.0-jdk21 AS builder

ARG SERVICE_MODULE

WORKDIR /workspace

COPY --chown=gradle:gradle . .

RUN gradle --no-daemon :${SERVICE_MODULE}:bootJar -x test \
    && cp "$(find ${SERVICE_MODULE}/build/libs -maxdepth 1 -name '*.jar' | head -n 1)" /tmp/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /tmp/app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
