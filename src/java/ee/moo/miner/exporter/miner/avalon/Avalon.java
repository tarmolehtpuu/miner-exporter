package ee.moo.miner.exporter.miner.avalon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.metrics.*;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Avalon implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    @Override
    public MinerConfig getConfig() {
        return config;
    }

    @Override
    public MinerType getType() {
        return MinerType.AVALON;
    }

    @Override
    public Metrics getMetrics() {
        var summary = getSummary();
        var stats = getStats();

        return Metrics.builder()
            .miner(getId())
            .type(getType())
            .uptime(summary.get("Elapsed").asInt())
            .accepted(summary.get("Accepted").asInt())
            .rejected(summary.get("Rejected").asInt())
            .found(summary.get("Found Blocks").asInt())
            .hashrate(summary.get("MHS 5s").asDouble())
            .temperature(getTemperature(stats))
            .fan(getFan(stats))
            .pool(getPool())
            .build();
    }

    private JsonNode getSummary() {
        var client = config.createTcpClient();

        try {
            var json = objectMapper.readTree(client.execute("summary"));

            validateHeader(json);
            validateSummary(json);

            return json.get("SUMMARY").get(0);

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private JsonNode getStats() {
        var client = config.createTcpClient();

        try {
            var body = client.execute("stats");
            System.out.println(body);
            var json = objectMapper.readTree(body);

            return json.get("STATS").get(0);

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private List<MetricsTemperature> getTemperature(JsonNode stats) {
        var result = new ArrayList<MetricsTemperature>();

        var pattern1 = Pattern.compile("Temp\\[([\\d.]+)]");
        var pattern2 = Pattern.compile("OTemp\\[([\\d.]+)]");

        var matcher1 = pattern1.matcher(stats.get("MM ID0").asText());
        if (matcher1.find()) {
            result.add(
                MetricsTemperature.builder()
                    .no(1)
                    .type(MetricsTemperatureType.CHIP)
                    .value(Double.parseDouble(matcher1.group(1)))
                    .build()
            );
        }

        var matcher2 = pattern2.matcher(stats.get("MM ID0").asText());
        if (matcher2.find()) {
            result.add(
                MetricsTemperature.builder()
                    .no(1)
                    .type(MetricsTemperatureType.PCB)
                    .value(Double.parseDouble(matcher2.group(1)))
                    .build()
            );
        }

        return result;
    }

    private List<MetricsFan> getFan(JsonNode stats) {
        var pattern = Pattern.compile("Fan1\\[([\\d.]+)]");
        var matcher = pattern.matcher(stats.get("MM ID0").asText());

        if (matcher.find()) {
            return List.of(MetricsFan.builder()
                .no(1)
                .value((int) Double.parseDouble(matcher.group(1)))
                .build()
            );
        }

        return List.of();
    }

    private List<MetricsPool> getPool() {
        var result = new ArrayList<MetricsPool>();
        var client = config.createTcpClient();

        try {
            var json = objectMapper.readTree(client.execute("pools"));

            System.out.println(json);

            validateHeader(json);
            validatePools(json);

            for (var pool : json.get("POOLS")) {
                result.add(MetricsPool.builder()
                    .no(pool.get("POOL").asInt())
                    .uri(pool.get("URL").asText())
                    .user(pool.get("User").asText())
                    .priority(pool.get("Priority").asInt())
                    .alive(pool.get("Status").asText().toLowerCase().contains("alive"))
                    .active(pool.get("Priority").asInt() == 0)
                    .accepted(pool.get("Accepted").asInt())
                    .rejected(pool.get("Rejected").asInt())
                    .build()
                );
            }

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }

        return result;
    }

    private void validateHeader(JsonNode json) {
        // FIXME
    }

    private void validateSummary(JsonNode json) {
        // FIXME
    }

    private void validatePools(JsonNode json) {

    }
}
