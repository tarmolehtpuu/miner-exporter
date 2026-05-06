package ee.moo.miner.exporter.miner.avalon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
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
            .hashrate(summary.get("MHS 5s").asDouble())
            .temperature(getTemperature(stats))
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

    private Double getTemperature(JsonNode stats) {
        var pattern = Pattern.compile("Temp\\[([\\d.]+)]");
        var matcher = pattern.matcher(stats.get("MM ID0").asText());
        return matcher.find()
            ? Double.parseDouble(matcher.group(1))
            : 0;
    }

    private void validateHeader(JsonNode json) {
        // FIXME
    }

    private void validateSummary(JsonNode json) {
        // FIXME
    }
}
