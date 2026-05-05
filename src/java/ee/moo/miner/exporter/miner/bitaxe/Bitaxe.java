package ee.moo.miner.exporter.miner.bitaxe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.client.ClientException;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerType;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Bitaxe implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    private final HttpClient client;

    public Bitaxe(MinerConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.client = config.createHttpClient();
        this.objectMapper = objectMapper;
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
    public Metrics getMetrics() {
        try {
            var response = client.newRequest(String.format("%s/api/system/info", config.getUri()))
                .timeout(config.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new ClientException("Unexpected http status code: %s (miner=%s, cmd=info)", response.getStatus(), config.getId());
            }

            System.out.println(response.getContentAsString());

            var json = objectMapper.readTree(response.getContent());

            validateInfo(json);

            var info = new BitaxeInfo();
            info.setCurrent(10843.75);
            info.setVoltage(5085.9375);
            info.setPower(17.4110107);

            return new BitaxeMetrics(info);

        } catch (InterruptedException | TimeoutException | ExecutionException | IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    private void validateInfo(JsonNode json) {
    }
}
