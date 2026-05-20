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

import ee.moo.miner.exporter.miner.*;
import ee.moo.miner.exporter.miner.MinerMetrics.Temperature.Type;
import ee.moo.tiny.common.util.StringUtil;
import ee.moo.tiny.json.Json;
import ee.moo.tiny.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings({"DuplicatedCode", "ClassCanBeRecord"})
public class Avalon implements Miner {

    private final MinerConfig config;

    public Avalon(MinerConfig config) {
        this.config = config;
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
    public MinerMetrics getMetrics() {
        var summary = getSummary();
        var stats = getStats();

        var metrics = new MinerMetrics();
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

    private JsonObject getSummary() {
        var client = config.createTcpClient();

        try {
            var json = Json.readObject(client.execute("summary"));

            validateHeader(json);
            validateSummary(json);

            return json.get("SUMMARY").asList().getFirst().asObject();

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private JsonObject getStats() {
        var client = config.createTcpClient();

        try {
            var body = client.execute("stats");
            var json = Json.readObject(body);

            validateHeader(json);
            validateStats(json);

            return json.get("STATS").asList().getFirst().asObject();

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private List<MinerMetrics.Temperature> getTemperatures(JsonObject stats) {
        var result = new ArrayList<MinerMetrics.Temperature>();

        var pattern1 = Pattern.compile("Temp\\[([\\d.]+)]");
        var pattern2 = Pattern.compile("OTemp\\[([\\d.]+)]");

        var matcher1 = pattern1.matcher(stats.get("MM ID0").asString());
        if (matcher1.find()) {
            result.add(new MinerMetrics.Temperature(1, Type.CHIP, Double.parseDouble(matcher1.group(1))));
        }

        var matcher2 = pattern2.matcher(stats.get("MM ID0").asString());
        if (matcher2.find()) {
            result.add(new MinerMetrics.Temperature(1, Type.PCB, Double.parseDouble(matcher2.group(1))));
        }

        return result;
    }

    private List<MinerMetrics.Fan> getFans(JsonObject stats) {
        var pattern = Pattern.compile("Fan1\\[([\\d.]+)]");
        var matcher = pattern.matcher(stats.get("MM ID0").asString());

        if (matcher.find()) {
            return List.of(new MinerMetrics.Fan(1, (int) Double.parseDouble(matcher.group(1))));
        }

        return List.of();
    }

    private List<MinerMetrics.Pool> getPools() {
        var result = new ArrayList<MinerMetrics.Pool>();
        var client = config.createTcpClient();

        try {
            var json = Json.readObject(client.execute("pools"));

            validateHeader(json);
            validatePools(json);

            for (var item : json.get("POOLS").asList()) {
                var p = item.asObject();
                var pool = new MinerMetrics.Pool();
                pool.setId(p.get("POOL").asInt());
                pool.setUri(p.get("URL").asString());
                pool.setUser(p.get("User").asString());
                pool.setPriority(p.get("Priority").asInt());
                pool.setAlive(p.get("Status").asString().toLowerCase().contains("alive"));
                pool.setActive(p.get("Priority").asInt() == 0);
                pool.setAccepted(p.get("Accepted").asInt());
                pool.setRejected(p.get("Rejected").asInt());

                result.add(pool);
            }

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }

        return result;
    }

    private void validateHeader(JsonObject json) {
        if (json.get("STATUS") == null) {
            throw new MinerException("Expecting response to contain field: STATUS");
        }

        if (!json.get("STATUS").isArray()) {
            throw new MinerException("Expecting response STATUS to be an array");
        }

        if (json.get("STATUS").asList().size() != 1) {
            throw new MinerException("Expecting response STATUS to contain exactly one element");
        }

        var status = json.get("STATUS")
            .asList()
            .getFirst()
            .asObject()
            .get("STATUS")
            .asString();

        if (!StringUtil.equals("S", status)) {
            throw new MinerException("Expecting response status to equal S, found: %s", status);
        }
    }

    private void validateSummary(JsonObject json) {
        if (json.get("SUMMARY") == null) {
            throw new MinerException("Expecting response to contain field: SUMMARY (cmd=summary)");
        }

        if (!json.get("SUMMARY").isArray()) {
            throw new MinerException("Expecting response to contain SUMMARY array (cmd=summary)");
        }

        if (json.get("SUMMARY").asList().size() != 1) {
            throw new MinerException("Expecting SUMMARY array to contain exactly one element (cmd=summary)");
        }

        if (!json.get("SUMMARY").asList().getFirst().isObject()) {
            throw new MinerException("Expecting an object in SUMMARY array (cmd=summary)");
        }
    }

    private void validateStats(JsonObject json) {
        if (json.get("STATS") == null) {
            throw new MinerException("Expecting response to contain field: STATS (cmd=stats)");
        }

        if (!json.get("STATS").isArray()) {
            throw new MinerException("Expecting response to contain STATS array (cmd=stats");
        }

        if (json.get("STATS").asList().size() < 2) {
            throw new MinerException("Expecting stats array to contain more than one element (cmd=stats)");
        }
    }

    private void validatePools(JsonObject json) {
        if (json.get("POOLS") == null) {
            throw new MinerException("Expecting response to contain field: POOLS (cmd=pools)");
        }

        if (!json.get("POOLS").isArray()) {
            throw new MinerException("Expecting response to contain POOLS array (cmd=pools)");
        }
    }

    @Override
    public String toString() {
        return String.format("Miner: id=%s, type=%s, uri=%s", getId(), getType(), getConfig().getUri());
    }
}
