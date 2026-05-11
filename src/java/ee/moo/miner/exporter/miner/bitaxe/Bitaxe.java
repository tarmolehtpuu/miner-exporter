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
package ee.moo.miner.exporter.miner.bitaxe;

import ee.moo.tiny.json.Json;
import ee.moo.tiny.json.JsonObject;
import ee.moo.miner.exporter.miner.*;
import ee.moo.miner.exporter.miner.MinerMetrics.Temperature.Type;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Bitaxe implements Miner {

    private final static int MAX_FAN_RPM = 7000;

    private final MinerConfig config;

    private final HttpClient client;

    public Bitaxe(MinerConfig config) {
        this.config = config;
        this.client = config.createHttpClient();
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
    public MinerMetrics getMetrics() {
        try {
            var request = HttpRequest
                .newBuilder()
                .uri(new URI(String.format("%s/api/system/info", config.getUri())))
                .timeout(config.getReadTimeout())
                .header("User-Agent", "miner-exporter/0.0.1")
                .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new MinerException("Unexpected http status code: %s (miner=%s, cmd=info)", response.statusCode(), config.getId());
            }

            var json = Json.read(response.body()).asObject();

            validate(json);

            var temp1 = new MinerMetrics.Temperature(1, Type.CHIP, json.get("temp").asDouble());
            var temp2 = new MinerMetrics.Temperature(2, Type.PCB, json.get("vrTemp").asDouble());

            var fan = new MinerMetrics.Fan(1, (int) (MAX_FAN_RPM * (json.get("fanspeed").asDouble() / 100.0)));

            var metrics = new MinerMetrics();
            metrics.setMiner(getId());
            metrics.setType(getType());
            metrics.setUptime(json.get("uptimeSeconds").asInt());
            metrics.setAccepted(json.get("sharesAccepted").asInt());
            metrics.setRejected(json.get("sharesRejected").asInt());
            metrics.setFound(json.get("blockFound").asInt());
            metrics.setHashrateGhs(json.get("hashRate").asDouble());
            metrics.setTemperatures(List.of(temp1, temp2));
            metrics.setFans(List.of(fan));
            metrics.setPools(getPools(json));

            return metrics;

        } catch (InterruptedException | IOException | URISyntaxException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    public List<MinerMetrics.Pool> getPools(JsonObject json) {
        var primary = json.get("isUsingFallbackStratum").asInt() == 0;

        var pool1 = new MinerMetrics.Pool();
        pool1.setId(0);
        pool1.setStratumUri(json.get("stratumURL").asString(), json.get("stratumPort").asInt());
        pool1.setUser(json.get("stratumUser").asString());
        pool1.setPriority(primary ? 0 : 1);
        pool1.setAlive(primary && json.get("hashRate").asDouble() > 0);
        pool1.setActive(primary);
        pool1.setAccepted(0);
        pool1.setRejected(0);

        var pool2 = new MinerMetrics.Pool();
        pool2.setId(1);
        pool2.setStratumUri(json.get("fallbackStratumURL").asString(), json.get("fallbackStratumPort").asInt());
        pool2.setUser(json.get("fallbackStratumUser").asString());
        pool2.setPriority(primary ? 1 : 0);
        pool2.setAlive(!primary && json.get("hashRate").asDouble() > 0);
        pool2.setActive(!primary);
        pool2.setAccepted(0);
        pool2.setRejected(0);

        return List.of(pool1, pool2);
    }

    private void validate(JsonObject json) {
        // FIXME
    }

    @Override
    public String toString() {
        return String.format("Miner: id=%s, type=%s, uri=%s", getId(), getType(), getConfig().getUri());
    }
}
