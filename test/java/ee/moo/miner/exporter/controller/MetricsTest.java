package ee.moo.miner.exporter.controller;

import ee.moo.miner.exporter.IntegrationTest;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.Getter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricsTest extends IntegrationTest {

    public static Stream<TestConfig> configProvider() throws URISyntaxException {
        var miner01 = new TestConfig("miner01", MinerType.BITAXE, new URI(wiremockUri()))
            .response("/miner01/metrics.txt")
            .wiremock("/api/system/info", "/miner01/info.json");

        var miner02 = new TestConfig("miner02", MinerType.AVALON, new URI(cgminerUri()))
            .response("/miner02/metrics.txt")
            .cgminer("pools", "/miner02/pools.json")
            .cgminer("stats", "/miner02/stats.json")
            .cgminer("summary", "/miner02/summary.json");

        var miner03 = new TestConfig("miner03", MinerType.ANTMINER, new URI(wiremockUri()))
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
            "MINER_ID", config.getMiner(),
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

        var response = http.newRequest(applicationUri("/metrics")).send();

        assertEquals(200, response.getStatus());
        assertEquals("text/plain; version=0.0.4; charset=utf-8", response.getHeaders().get("Content-Type"));

        var lines1 = config
            .getResponse()
            .lines()
            .toList();

        var lines2 = response
            .getContentAsString()
            .lines()
            .toList();

        assertEquals(lines1.size(), lines2.size());

        for (int i = 0; i < lines1.size(); i++) {
            assertEquals(lines1.get(i), lines2.get(i));
        }
    }

    public static class TestConfig {

        @Getter
        private final String miner;

        @Getter
        private final MinerType type;

        @Getter
        private final URI uri;

        private final Map<String, String> wiremock = new HashMap<>();

        private final Map<String, String> cgminer = new HashMap<>();

        @Getter
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

        @Override
        public String toString() {
            return String.format("%s (%s)", miner, type.toString());
        }
    }
}
