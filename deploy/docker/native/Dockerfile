FROM alpine:3.23.4

RUN mkdir /app
WORKDIR /app

COPY build/libs/miner-exporter-linux-amd64 /app/miner-exporter

ENV LISTEN_ADDRESS="0.0.0.0"
ENV LISTEN_PORT="9041"

ENTRYPOINT ["/app/miner-exporter"]
