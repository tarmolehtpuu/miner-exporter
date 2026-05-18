FROM azul/zulu-openjdk-alpine:25-latest

WORKDIR /app
COPY build/libs/app.jar app.jar

ENV LISTEN_HOST=0.0.0.0
ENV LISTEN_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-server", "-XX:+UseContainerSupport", "-XX:MinRAMPercentage=50.0", "-XX:MaxRAMPercentage=80.0", "-server", "-jar", "app.jar"]
