package ee.moo.miner.exporter.miner.nano3;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.client.CGMinerConnection;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerException;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class Nano3 implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    @Override
    public MinerConfig getConfig() {
        return config;
    }

    @Override
    public MinerType getType() {
        return MinerType.NANO3;
    }

    @Override
    public Metrics getMetrics() {
        var connection = new CGMinerConnection(config.getHost(), config.getPort());

        connection.setObjectMapper(objectMapper);
        connection.setConnectTimeout(config.getConnectTimeout());
        connection.setReadTimeout(config.getReadTimeout());

        try {
            connection.connect();

            var version = connection.getVersion();
            System.out.println(version);

        } catch (IOException e1) {
            try {
                connection.close();
            } catch (Exception e2) {
                // ignored
            }

            throw new MinerException(e1.getMessage(), e1);
        }

        return new Nano3Metrics();
    }
}
