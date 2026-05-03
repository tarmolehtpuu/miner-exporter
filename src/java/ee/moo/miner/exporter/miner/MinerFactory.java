package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.bitaxe.Bitaxe;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;


@RequiredArgsConstructor
public class MinerFactory {

    private static final Duration CONNECT_TIMEOUT = Duration.ofMillis(2000);
    private static final Duration READ_TIMEOUT = Duration.ofMillis(2000);

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    @SuppressWarnings({"EnhancedSwitchMigration", "SwitchStatementWithTooFewBranches"})
    public Miner create(MinerConfig config) {
        switch (config.getType()) {
            case BITAXE:
                return createBitaxe(config);
            default:
                throw new MinerException("Unsupported miner type: %s", config.getType());
        }
    }

    private Miner createBitaxe(MinerConfig config) {
        var rf = new JettyClientHttpRequestFactory(httpClient);
        rf.setConnectTimeout(CONNECT_TIMEOUT);
        rf.setReadTimeout(READ_TIMEOUT);

        var client = RestClient.builder()
            .requestFactory(rf)
            .baseUrl(String.format("http://%s:%d", config.getHost(), config.getPort()))
            .build();

        return new Bitaxe(config, client, objectMapper);
    }
}
