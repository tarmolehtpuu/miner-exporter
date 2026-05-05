package ee.moo.miner.exporter.miner.nano3;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.client.cgminer.CGMinerClient;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.RequiredArgsConstructor;

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
        var client = new CGMinerClient(config.getHost(), config.getPort());

        client.setObjectMapper(objectMapper);
        client.setConnectTimeout(config.getConnectTimeout());
        client.setReadTimeout(config.getReadTimeout());

        var version = client.getVersion();
        System.out.println(version);

        var summary = client.getSummary();
        System.out.println(summary);

        var config = client.getConfig();
        System.out.println(config);

        var pools = client.getPools();
        for (var pool : pools) {
            System.out.println(pool);
        }

        return new Nano3Metrics();
    }
}
