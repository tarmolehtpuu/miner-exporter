package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.bitaxe.Bitaxe;
import ee.moo.miner.exporter.miner.nano3.Nano3;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


@RequiredArgsConstructor
public class MinerFactory {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    @SuppressWarnings({"EnhancedSwitchMigration"})
    public Miner create(MinerConfig config) {
        switch (config.getType()) {
            case BITAXE:
                return createBitaxe(config);
            case NANO3:
                return createNano3(config);
            default:
                throw new MinerException("Unsupported miner type: %s", config.getType());
        }
    }

    private Miner createBitaxe(MinerConfig config) {
        var rf = new JettyClientHttpRequestFactory(httpClient);
        rf.setConnectTimeout(config.getConnectTimeout());
        rf.setReadTimeout(config.getReadTimeout());

        var client = RestClient.builder()
            .requestFactory(rf)
            .baseUrl(String.format("http://%s:%d", config.getHost(), config.getPort()))
            .build();

        return new Bitaxe(config, client, objectMapper);
    }

    private Miner createNano3(MinerConfig config) {
        return new Nano3(config, objectMapper);
    }
}
