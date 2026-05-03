package ee.moo.miner.exporter.miner;

import ee.moo.miner.exporter.common.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BitaxeTest extends IntegrationTest {

    @Autowired
    private MinerFactory minerFactory;

    private Miner miner;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        miner = minerFactory.create(
            MinerConfig.builder()
                .id("miner01")
                .type(MinerType.BITAXE)
                .host("127.0.0.1")
                .port(8082)
                .build()
        );
    }

    @Test
    public void testGetMetrics() throws IOException {
        var json = new ClassPathResource("/miner/bitaxe/info.json")
            .getContentAsString(StandardCharsets.UTF_8);

        wiremock.stubFor(get(urlEqualTo("/api/system/info")).willReturn(okJson(json)));

        var metrics = miner.getMetrics();

        assertEquals(17.4110107, metrics.getPower());
        assertEquals(5085.9375, metrics.getVoltage());
        assertEquals(10843.75, metrics.getCurrent());
    }
}
