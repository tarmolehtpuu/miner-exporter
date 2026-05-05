package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class BitaxeTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Miner miner;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();

        try {
            var config = new MinerConfig();
            config.setId("miner01");
            config.setType(MinerType.BITAXE);
            config.setUri(new URI("tcp://127.0.0.1:8082"));

            miner = config.getType().create(config, objectMapper);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
