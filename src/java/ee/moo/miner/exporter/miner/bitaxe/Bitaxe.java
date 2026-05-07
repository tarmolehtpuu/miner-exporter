package ee.moo.miner.exporter.miner.bitaxe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.metrics.*;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Bitaxe implements Miner {

    private final static int MAX_FAN_RPM = 7000;

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    private final HttpClient client;

    public Bitaxe(MinerConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.client = config.createHttpClient();
        this.objectMapper = objectMapper;
    }

    @Override
    public MinerConfig getConfig() {
        return config;
    }

    @Override
    public MinerType getType() {
        return MinerType.BITAXE;
    }

    @Override
    public Metrics getMetrics() {
        try {
            var response = client.newRequest(String.format("%s/api/system/info", config.getUri()))
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new MinerException("Unexpected http status code: %s (miner=%s, cmd=info)", response.getStatus(), config.getId());
            }

            System.out.println(response.getContentAsString());

            var json = objectMapper.readTree(response.getContent());

            validate(json);

            var temp1 = MetricsTemperature.builder()
                .no(1)
                .type(MetricsTemperatureType.CHIP)
                .value(json.get("temp").asDouble())
                .build();
            var temp2 = MetricsTemperature.builder()
                .no(2)
                .type(MetricsTemperatureType.PCB)
                .value(json.get("vrTemp").asDouble())
                .build();


            var fan = MetricsFan.builder()
                .no(1)
                .value((int) (MAX_FAN_RPM * (json.get("fanspeed").asDouble() / 100.0)))
                .build();

            return Metrics.builder()
                .miner(getId())
                .type(getType())
                .uptime(json.get("uptimeSeconds").asInt())
                .accepted(json.get("sharesAccepted").asInt())
                .rejected(json.get("sharesRejected").asInt())
                .found(json.get("blockFound").asInt())
                .hashrate(json.get("hashRate").asDouble())
                .temperature(List.of(temp1, temp2))
                .fan(List.of(fan))
                .pool(getPool(json))
                .build();

        } catch (InterruptedException | TimeoutException | ExecutionException | IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    public List<MetricsPool> getPool(JsonNode json) {
        var primary = json.get("isUsingFallbackStratum").asInt() == 0;

        var pool1 = MetricsPool.builder()
            .no(0)
            .uri(String.format(
                "stratum+tcp://%s:%d",
                json.get("stratumURL").asText(),
                json.get("stratumPort").asInt()
            ))
            .user(json.get("stratumUser").asText())
            .priority(primary ? 0 : 1)
            .alive(primary && json.get("hashRate").asDouble() > 0)
            .active(primary)
            .accepted(0)
            .rejected(0)
            .build();

        var pool2 = MetricsPool.builder()
            .no(1)
            .uri(String.format(
                "stratum+tcp://%s:%d",
                json.get("fallbackStratumURL").asText(),
                json.get("fallbackStratumPort").asInt()
            ))
            .user(json.get("fallbackStratumUser").asText())
            .priority(primary ? 1 : 0)
            .alive(!primary && json.get("hashRate").asDouble() > 0)
            .active(!primary)
            .accepted(0)
            .rejected(0)
            .build();

        return List.of(pool1, pool2);
    }

    private void validate(JsonNode json) {
        // FIXME
    }
}
