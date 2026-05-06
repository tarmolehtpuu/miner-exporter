package ee.moo.miner.exporter.miner.antminer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.metrics.MetricsFan;
import ee.moo.miner.exporter.metrics.MetricsTemperature;
import ee.moo.miner.exporter.metrics.MetricsTemperatureType;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class Antminer implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    private final HttpClient client;

    public Antminer(MinerConfig config, ObjectMapper objectMapper) {
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
        return MinerType.ANTMINER;
    }

    @Override
    public Metrics getMetrics() {
        var summary = getSummary();
        System.out.println(summary);

        var stats = getStats();
        System.out.println(stats);

        return Metrics.builder()
            .miner(getId())
            .type(getType())
            .uptime(summary.get("Elapsed").asInt())
            .accepted(summary.get("Accepted").asInt())
            .rejected(summary.get("Rejected").asInt())
            .hashrate(summary.get("GHS 5s").asDouble())
            .temperature(getTemperature(stats))
            .fan(getFan(stats))
            .build();
    }

    private JsonNode getSummary() {
        try {
            var response = client.newRequest(String.format("%s/cgi-bin/miner_summary.cgi", config.getUri()))
                .timeout(config.getReadTimeout().toMillisPart(), TimeUnit.MILLISECONDS)
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new MinerException("Unexpected http status code: %s (miner=%s, cmd=summary)", response.getStatus(), config.getId());
            }

            var json = objectMapper.readTree(response.getContent());

            validateHeader(json);
            validateSummary(json);

            return json.get("SUMMARY").get(0);

        } catch (JsonMappingException e) {
            throw new MinerException(e.getMessage(), e);
        } catch (ExecutionException | InterruptedException | TimeoutException | IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private JsonNode getStats() {
        try {
            var response = client.newRequest(String.format("%s/cgi-bin/miner_stats.cgi", config.getUri()))
                .timeout(config.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new MinerException("Unexpected http status code: %s (miner=%s, cmd=stats)", response.getStatus(), config.getId());
            }

            System.out.println(response.getContentAsString());

            var json = objectMapper.readTree(response.getContent());

            validateHeader(json);
            validateStats(json);

            return json.get("STATS").get(1);

        } catch (ExecutionException | IOException | TimeoutException | InterruptedException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private List<MetricsTemperature> getTemperature(JsonNode json) {
        var result = new ArrayList<MetricsTemperature>();

        for (var i = 1; i <= json.get("temp_num").asInt(); i++) {
            var temp1 = json.get(String.format("temp%s", i)).asDouble();
            var temp2 = json.get(String.format("temp2_%s", i)).asDouble();

            if (temp1 > 0) {
                result.add(
                    MetricsTemperature.builder()
                        .no(i)
                        .type(MetricsTemperatureType.PCB)
                        .value(temp1)
                        .build()
                );
            }

            if (temp2 > 0) {
                result.add(
                    MetricsTemperature.builder()
                        .no(i)
                        .type(MetricsTemperatureType.CHIP)
                        .value(temp2)
                        .build()
                );
            }
        }

        return result;
    }

    private List<MetricsFan> getFan(JsonNode json) {
        var result = new ArrayList<MetricsFan>();

        for (int i = 1; i <= json.get("fan_num").asInt(); i++) {
            result.add(
                MetricsFan.builder()
                    .no(i)
                    .value(json.get(String.format("fan%d", i)).asInt())
                    .build()
            );

        }

        return result;
    }

    private void validateHeader(JsonNode json) {
    }

    private void validateSummary(JsonNode json) {
        if (json.get("SUMMARY").getNodeType() != JsonNodeType.ARRAY) {
            throw new MinerException("Expecting response to contain SUMMARY array (cmd=%s)", "summary");
        }

        if (json.get("SUMMARY").size() != 1) {
            throw new MinerException("Expecting SUMMARY array to contain exactly one element (cmd=%s)", "summary");
        }

        if (json.get("SUMMARY").get(0).getNodeType() != JsonNodeType.OBJECT) {
            throw new MinerException("Expecting an object in SUMMARY array (cmd=%s)", "summary");
        }
    }

    private void validateStats(JsonNode json) {
    }
}
