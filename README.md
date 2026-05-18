# miner-exporter ![Static Badge](https://img.shields.io/badge/version-0.1.7-blue) ![Endpoint Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Ftarmolehtpuu%2Fa5debb67ba982255c7f79caec03d734c%2Fraw%2F869c2650d5a66ea12e11d27d7b5b52096702f50b%2Fminer-exporter-junit-tests.json) ![Endpoint Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Ftarmolehtpuu%2Fa5debb67ba982255c7f79caec03d734c%2Fraw%2F869c2650d5a66ea12e11d27d7b5b52096702f50b%2Fminer-exporter-jacoco-coverage.json)

Prometheus exporters for various crypto miners. Only implemented the ones I am currently using, but feel free to send
PR-s for others.

The following miners are currently supported:

- ANTMINER
- AVALON
- BITAXE

Each miner-exporter instance is designed to monitor a single miner. If possible install it directly on the miner, but
since that can be a bit complicated it is also alright to run the exporters on a single Raspberry Pi or similar.

### Metrics

| Metric                        | Type    | Description                                  |
|-------------------------------|---------|----------------------------------------------|
| **miner_uptime_total**        | counter | Time since last boot in seconds              |
| **miner_accepted_total**      | counter | Total number of shares accepted              |
| **miner_rejected_total**      | counter | Total number of shares rejected              |
| **miner_found_total**         | counter | Total number of blocks found                 |
| **miner_hashrate**            | gauge   | Current miner hashrate in TH/s               |
| **miner_temperature**         | gauge   | Current miner temperature in C               |
| **miner_fan_rpm**             | gauge   | Current miner fan RPM                        |
| **miner_pool_alive**          | gauge   | Current pool liveness                        |
| **miner_pool_active**         | gauge   | If pool is currently active                  |
| **miner_pool_accepted_total** | counter | Total numer of shares accepted for the pool  |
| **miner_pool_rejected_total** | counter | Total number of shares rejected for the pool |

### Labels

| LABEL                | COMMENT             | SECTION     |
|----------------------|---------------------|-------------|
| **miner**            | Miner ID from ENV   |             |
| **type**             | Miner Type from ENV |             |
| **board**            | Board number        | TEMPERATURE |
| **temperature_type** | CHIP, PCB           | TEMPERATURE |
| **fan**              | Fan number          | FAN         |
| **pool**             | Pool number         | POOL        |
| **pool_priority**    | Pool priority       | POOL        |
| **pool_uri**         | Pool URI            | POOL        |
| **pool_user**        | Pool user           | POOL        |

### Environment

Miners are configured via the following environment variables.

| NAME                 | REQUIRED | DEFAULT | COMMENT                        |
|----------------------|----------|---------|--------------------------------|
| **MINER_ID**         | Y        | -       | Example: miner01               |
| **MINER_TYPE**       | Y        | -       | ANTMINER, AVALON, BITAXE       |
| **MINER_URI**        | Y        | -       | Example: tcp://10.10.10.1:4028 |
| **MINER_AUTH**       | -        | NONE    | NONE, BASIC, DIGEST            |
| **MINER_USERNAME**   | -        | -       |                                |
| **MINER_PASSWORD**   | -        | -       |                                |
| **CONNECT_TIMEOUT**  | -        | 1s      |                                |
| **READ_TIMEOUT**     | -        | 4s      |                                |
| **READ_BUFFER_SIZE** | -        | 8192    |                                |
| **LISTEN_ADDR**      | -        | 0.0.0.0 |                                |
| **LISTEN_PORT**      | -        | 8080    |                                |
| **LOG_LEVEL**        | -        | INFO    | DEBUG, INFO, WARN, ERROR       |
| **LOG_FORMAT**       | -        | TEXT    | TEXT, JSONL                    |


## Running

### Java

```bash

# Example 1: BITAXE 
MINER_ID=miner01 MINER_TYPE=BITAXE MINER_URI=http://10.10.10.1 \
  java -Xms128m -Xmx256m -server -jar miner-exporter.jar

# Example 2: AVALON
MINER_ID=miner02 MINER_TYPE=AVALON MINER_URI=tcp://10.10.10.2:4028 \
  java -Xms128m -Xmx256m -server -jar miner-exporter.jar
  
# Example 3: ANTMINER
MINER_ID=miner03 MINER_TYPE=ANTMINER MINER_URI=http://10.10.10.3 \
  MINER_AUTH=DIGEST MINER_USERNAME=username MINER_PASSWORD=password \
  java -Xms128m -Xmx256m -server -jar miner-exporter.jar
```

### Systemd (JAR)

```systemd
[Unit]
Description=Prometheus Miner Exporter
Documentation=https://github.com/tarmolehtpuu/miner-exporter

[Service]
Type=simple
Restart=on-failure
User=prometheus
Group=prometheus
EnvironmentFile=/etc/default/miner-exporter-miner01
SuccessExitStatus=143
ExecStart=/usr/bin/java -Xms128m -Xmx256m -server \
    -jar /opt/miner-exporter/miner-exporter.jar
ExecStop=/bin/kill -15 $MAINPID
StandardOutput=append:/var/log/prometheus/miner01.log
StandardError=append:/var/log/prometheus/miner01.log
TimeoutStopSec=20s
SendSIGKILL=no

[Install]
WantedBy=multi-user.target
```

### Systemd (Native)

```systemd
[Unit]
Description=Prometheus Miner Exporter
Documentation=https://github.com/tarmolehtpuu/miner-exporter

[Service]
Type=simple
Restart=on-failure
User=prometheus
Group=prometheus
EnvironmentFile=/etc/default/miner-exporter-miner02
ExecStart=/usr/local/bin/miner-exporter
StandardOutput=append:/var/log/prometheus/miner02.log
StandardError=append:/var/log/prometheus/miner02.log
TimeoutStopSec=20s
SendSIGKILL=no

[Install]
WantedBy=multi-user.target
```

### Docker

- **TODO**

## Prometheus

Add one static config for all the miner-exporters. If running all of them on localhost then run them on separate ports so configuring Prometheus is less of a hassle.

```yaml
- job_name: miner-exporter
  static_configs:
    - targets:
        - localhost:9041
        - localhost:9042
        - localhost:9043
        - localhost:9044
        - ...

```

## Grafana

- **TODO**