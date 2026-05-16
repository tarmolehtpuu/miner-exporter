# miner-exporter ![Static Badge](https://img.shields.io/badge/version-0.1.0-blue) ![Endpoint Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Ftarmolehtpuu%2Fa5debb67ba982255c7f79caec03d734c%2Fraw%2F869c2650d5a66ea12e11d27d7b5b52096702f50b%2Fminer-exporter-junit-tests.json) ![Endpoint Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Ftarmolehtpuu%2Fa5debb67ba982255c7f79caec03d734c%2Fraw%2F869c2650d5a66ea12e11d27d7b5b52096702f50b%2Fminer-exporter-jacoco-coverage.json)

Prometheus exporters for various crypto miners. Only implemented the ones I am currently using, but feel free to send PR-s for others (or just send me one miner for testing/dev and I can add support for it).

The following miners are currently supported:
- ANTMINER
- AVALON
- BITAXE

Each miner-exporter instance is designed to monitor a single miner. If possible install it directly on the miner, but since that
can be a bit complicated it is also alright to run the exporters on a single Raspberry PI or similar.

### Metrics

| Metric                        | Type      | Description                                  |
|-------------------------------|-----------|----------------------------------------------|
| **miner_uptime_total**        | counter   | Time since last boot in seconds              |
| **miner_accepted_total**      | counter   | Total number of shares accepted              |
| **miner_rejected_total**      | counter   | Total number of shares rejected              |
| **miner_found_total**         | counter   | Total number of blocks found                 |
| **miner_hashrate**            | gauge     | Current miner hashrate in TH/s               |
| **miner_temperature**         | gauge     | Current miner temperature in C               |
| **miner_fan_rpm**             | gauge     | Current miner fan RPM                        |
| **miner_pool_alive**          | gauge     | Current pool liveness                        |
| **miner_pool_active**         | gauge     | If pool is currently active                  |
| **miner_pool_accepted_total** | counter   | Total numer of shares accepted for the pool  |
| **miner_pool_rejected_total** | counter   | Total number of shares rejected for the pool |

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
| **MINER_ID**         | Y        | NULL    | Example: miner01               |
| **MINER_TYPE**       | Y        | NULL    | ANTMINER, AVALON, BITAXE       |
| **MINER_URI**        | Y        | NULL    | Example: tcp://10.10.10.1:4028 |
| **MINER_AUTH**       | -        | NONE    | NONE, BASIC, DIGEST            |
| **MINER_USERNAME**   | -        | NULL    |                                |
| **MINER_PASSWORD**   | -        | NULL    |                                |
| **CONNECT_TIMEOUT**  | -        | 1s      |                                |
| **READ_TIMEOUT**     | -        | 4s      |                                |
| **READ_BUFFER_SIZE** | -        | 8192    |                                |
| **LISTEN_ADDR**      | -        | 0.0.0.0 |                                |
| **LISTEN_PORT**      | -        | 8080    |                                |


