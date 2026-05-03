package ee.moo.miner.exporter.miner.bitaxe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

@AllArgsConstructor
public class Bitaxe implements Miner {

    private final MinerConfig config;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Override
    public MinerConfig getConfig() {
        return config;
    }

    @Override
    public MinerType getType() {
        return MinerType.BITAXE;
    }

    @Override
    public Metrics getMetrics() {
        var response = restClient.get()
            .uri("/api/system/info")
            .retrieve()
            .toEntity(String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new MinerException("Unable to fetch data, HTTP error : %s", response.getStatusCode());
        }

        try {
            return new BitaxeMetrics(objectMapper.readValue(response.getBody(), BitaxeInfo.class));

        } catch (JsonProcessingException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }
}
