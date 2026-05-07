package ee.moo.miner.exporter.controller;

import ee.moo.miner.exporter.common.IntegrationTest;
import ee.moo.miner.exporter.miner.MinerType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricsTest extends IntegrationTest {

    public static Stream<TestConfig> configProvider() {
        return Stream.of(
            new TestConfig("miner01", MinerType.BITAXE)
                .response("/bitaxe/response.txt")
                .wiremock("/api/system/info", "/bitaxe/info.json")
        );
    }

    @ParameterizedTest
    @MethodSource("configProvider")
    public void testMiners(TestConfig config) throws Exception {
        for (var path : config.getWiremockPaths()) {
            wiremock.stubFor(get(urlEqualTo(path)).willReturn(okJson(config.getWiremockReply(path))));
        }

        var response = http
            .newRequest(applicationUri(String.format("/metrics/%s", config.getMiner())))
            .send();

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

        private final String miner;

        private final MinerType type;

        private final Map<String, String> wiremock = new HashMap<>();

        private String response;

        public TestConfig(String miner, MinerType type) {
            this.miner = miner;
            this.type = type;
        }

        public String getMiner() {
            return miner;
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

        public TestConfig response(String file) {
            try {
                response = IntegrationTest.resource(file);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return this;
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
