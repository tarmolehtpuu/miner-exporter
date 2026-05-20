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
package ee.moo.miner.exporter.controller;

import ee.moo.miner.exporter.IntegrationTest;
import ee.moo.miner.exporter.miner.MinerType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricsTest extends IntegrationTest {

    public static Stream<TestConfig> configProvider() throws URISyntaxException {
        var miner01 = new TestConfig("miner01", MinerType.BITAXE, wiremockUri())
            .response("/miner01/metrics.txt")
            .wiremock("/api/system/info", "/miner01/info.json");

        var miner02 = new TestConfig("miner02", MinerType.AVALON, cgminerUri())
            .response("/miner02/metrics.txt")
            .cgminer("pools", "/miner02/pools.json")
            .cgminer("stats", "/miner02/stats.json")
            .cgminer("summary", "/miner02/summary.json");

        var miner03 = new TestConfig("miner03", MinerType.ANTMINER, wiremockUri())
            .response("/miner03/metrics.txt")
            .wiremock("/cgi-bin/miner_pools.cgi", "/miner03/pools.json")
            .wiremock("/cgi-bin/miner_stats.cgi", "/miner03/stats.json")
            .wiremock("/cgi-bin/miner_summary.cgi", "/miner03/summary.json");

        return Stream.of(
            miner01,
            miner02,
            miner03
        );
    }

    @ParameterizedTest
    @MethodSource("configProvider")
    public void testMiners(TestConfig config) throws Exception {
        var env = Map.of(
            "MINER_ID", config.miner,
            "MINER_TYPE", config.getType().toString(),
            "MINER_URI", config.getUri().toString()
        );

        startApplication(env, APPLICATION_HOST, APPLICATION_PORT);

        for (var path : config.getWiremockPaths()) {
            wiremock.stubFor(get(urlEqualTo(path)).willReturn(okJson(config.getWiremockReply(path))));
        }

        for (var cmd : config.getCgminerCommands()) {
            cgminer.stub(cmd, config.getCgminerReply(cmd));
        }

        var request = HttpRequest.newBuilder()
            .timeout(Duration.ofMillis(2000))
            .uri(applicationUri("/metrics"))
            .GET()
            .build();

        var response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(
            "text/plain; version=0.0.4; charset=utf-8",
            response.headers().firstValue("Content-Type").orElseThrow()
        );

        var lines1 = config
            .getResponse()
            .lines()
            .toList();

        var lines2 = response
            .body()
            .lines()
            .toList();

        assertEquals(lines1.size(), lines2.size());

        for (int i = 0; i < lines1.size(); i++) {
            assertEquals(lines1.get(i), lines2.get(i));
        }
    }

    public static class TestConfig {

        private final String miner;

        private final MinerType type;

        private final URI uri;

        private final Map<String, String> wiremock = new HashMap<>();

        private final Map<String, String> cgminer = new HashMap<>();

        private String response;

        public TestConfig(String miner, MinerType type, URI uri) {
            this.miner = miner;
            this.type = type;
            this.uri = uri;
        }

        public TestConfig wiremock(String path, String file) {
            try {
                wiremock.put(path, IntegrationTest.resource(file));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return this;
        }

        public Set<String> getWiremockPaths() {
            return wiremock.keySet();
        }

        public String getWiremockReply(String path) {
            return wiremock.get(path);
        }

        public TestConfig cgminer(String command, String file) {
            try {
                cgminer.put(command, IntegrationTest.resource(file));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return this;
        }

        public Set<String> getCgminerCommands() {
            return cgminer.keySet();
        }

        public String getCgminerReply(String command) {
            return cgminer.get(command);
        }

        public TestConfig response(String file) {
            try {
                response = IntegrationTest.resource(file);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return this;
        }

        public String getMiner() {
            return miner;
        }

        public MinerType getType() {
            return type;
        }

        public URI getUri() {
            return uri;
        }

        public String getResponse() {
            return response;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", miner, type.toString());
        }
    }
}
