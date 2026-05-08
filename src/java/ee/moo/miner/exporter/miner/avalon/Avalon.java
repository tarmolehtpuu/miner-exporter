/*
   miner-exporter - Prometheus exporter for cryptocurrency miners
   Copyright 2026 Tarmo Lehtpuu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ee.moo.miner.exporter.miner.avalon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Avalon implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    public Avalon(MinerConfig config) {
        this.config = config;
        this.objectMapper = config.createObjectMapper();
    }

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

        var metrics = new Metrics();
        metrics.setMiner(getId());
        metrics.setType(getType());
        metrics.setUptime(summary.get("Elapsed").asInt());
        metrics.setAccepted(summary.get("Accepted").asInt());
        metrics.setRejected(summary.get("Rejected").asInt());
        metrics.setFound(summary.get("Found Blocks").asInt());
        metrics.setHashrateMhs(summary.get("MHS 5s").asDouble());
        metrics.setTemperatures(getTemperatures(stats));
        metrics.setFans(getFans(stats));
        metrics.setPools(getPools());

        return metrics;
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
            var json = objectMapper.readTree(body);

            return json.get("STATS").get(0);

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private List<Metrics.Temperature> getTemperatures(JsonNode stats) {
        var result = new ArrayList<Metrics.Temperature>();

        var pattern1 = Pattern.compile("Temp\\[([\\d.]+)]");
        var pattern2 = Pattern.compile("OTemp\\[([\\d.]+)]");

        var matcher1 = pattern1.matcher(stats.get("MM ID0").asText());
        if (matcher1.find()) {
            result.add(
                Metrics.Temperature.builder()
                    .id(1)
                    .type(Metrics.Temperature.Type.CHIP)
                    .value(Double.parseDouble(matcher1.group(1)))
                    .build()
            );
        }

        var matcher2 = pattern2.matcher(stats.get("MM ID0").asText());
        if (matcher2.find()) {
            result.add(
                Metrics.Temperature.builder()
                    .id(1)
                    .type(Metrics.Temperature.Type.PCB)
                    .value(Double.parseDouble(matcher2.group(1)))
                    .build()
            );
        }

        return result;
    }

    private List<Metrics.Fan> getFans(JsonNode stats) {
        var pattern = Pattern.compile("Fan1\\[([\\d.]+)]");
        var matcher = pattern.matcher(stats.get("MM ID0").asText());

        if (matcher.find()) {
            return List.of(Metrics.Fan.builder()
                .id(1)
                .value((int) Double.parseDouble(matcher.group(1)))
                .build()
            );
        }

        return List.of();
    }

    private List<Metrics.Pool> getPools() {
        var result = new ArrayList<Metrics.Pool>();
        var client = config.createTcpClient();

        try {
            var json = objectMapper.readTree(client.execute("pools"));

            validateHeader(json);
            validatePools(json);

            for (var pool : json.get("POOLS")) {
                result.add(Metrics.Pool.builder()
                    .id(pool.get("POOL").asInt())
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
