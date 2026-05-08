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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.MinerMetrics;
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

    public Bitaxe(MinerConfig config) {
        this.config = config;
        this.client = config.createHttpClient();
        this.objectMapper = config.createObjectMapper();
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
            var response = client.newRequest(String.format("%s/api/system/info", config.getUri()))
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new MinerException("Unexpected http status code: %s (miner=%s, cmd=info)", response.getStatus(), config.getId());
            }

            var json = objectMapper.readTree(response.getContent());

            validate(json);

            var temp1 = MinerMetrics.Temperature.builder()
                .id(1)
                .type(MinerMetrics.Temperature.Type.CHIP)
                .value(json.get("temp").asDouble())
                .build();
            var temp2 = MinerMetrics.Temperature.builder()
                .id(2)
                .type(MinerMetrics.Temperature.Type.PCB)
                .value(json.get("vrTemp").asDouble())
                .build();


            var fan = MinerMetrics.Fan.builder()
                .id(1)
                .value((int) (MAX_FAN_RPM * (json.get("fanspeed").asDouble() / 100.0)))
                .build();

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

        } catch (InterruptedException | TimeoutException | ExecutionException | IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    public List<MinerMetrics.Pool> getPools(JsonNode json) {
        var primary = json.get("isUsingFallbackStratum").asInt() == 0;

        var pool1 = MinerMetrics.Pool.builder()
            .id(0)
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

        var pool2 = MinerMetrics.Pool.builder()
            .id(1)
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
