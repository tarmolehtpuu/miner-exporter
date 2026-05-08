FROM azul/zulu-openjdk-alpine:26-latest

WORKDIR /app

COPY build/libs/app.jar app.jar

ENV LISTEN_ADDRESS="0.0.0.0"
ENV LISTEN_PORT="9041"

ENTRYPOINT ["java", "-server", "-XX:InitialRAMPercentage=50.0", "-XX:MaxRAMPercentage=80.0", "-jar", "app.jar"]
