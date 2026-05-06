package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.common.IntegrationTest;
import ee.moo.miner.exporter.metrics.MetricsFan;
import ee.moo.miner.exporter.metrics.MetricsPool;
import ee.moo.miner.exporter.metrics.MetricsTemperature;
import ee.moo.miner.exporter.metrics.MetricsTemperatureType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitaxeTest extends IntegrationTest {

    private Miner miner;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();

        try {
            var config = new MinerConfig();
            config.setId("miner01");
            config.setType(MinerType.BITAXE);
            config.setUri(new URI("http://127.0.0.1:8082"));

            miner = config.getType().create(config, new ObjectMapper());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void testGetMetrics() throws IOException {
        var json = resource("/miner/bitaxe/info.json");
        System.out.println(json);

        wiremock.stubFor(get(urlEqualTo("/api/system/info")).willReturn(okJson(json)));

        var metrics = miner.getMetrics();

        assertEquals("miner01", metrics.getMiner());
        assertEquals(MinerType.BITAXE, metrics.getType());
        assertEquals(133287, metrics.getUptime());
        assertEquals(32946, metrics.getAccepted());
        assertEquals(13, metrics.getRejected());
        assertEquals(0, metrics.getFound());
        assertEquals(1100.1434326, metrics.getHashrate());

        assertEquals(
            List.of(
                MetricsTemperature.builder()
                    .no(1)
                    .type(MetricsTemperatureType.CHIP)
                    .value(70.125)
                    .build(),
                MetricsTemperature.builder()
                    .no(2)
                    .type(MetricsTemperatureType.PCB)
                    .value(56.0)
                    .build()
            ),
            metrics.getTemperature()
        );
        assertEquals(
            List.of(
                MetricsFan.builder()
                    .no(1)
                    .value(7000)
                    .build()
            ),
            metrics.getFan()
        );
        assertEquals(
            List.of(
                MetricsPool.builder()
                    .no(0)
                    .uri("stratum+tcp://bch.viabtc.io:3333")
                    .user("natte.miner05")
                    .priority(0)
                    .alive(true)
                    .active(true)
                    .accepted(0)
                    .rejected(0)
                    .build(),
                MetricsPool.builder()
                    .no(1)
                    .uri("stratum+tcp://btc.viabtc.io:3333")
                    .user("natte.miner05")
                    .priority(1)
                    .alive(false)
                    .active(false)
                    .accepted(0)
                    .rejected(0)
                    .build()
            ),
            metrics.getPool()
        );
    }
}
