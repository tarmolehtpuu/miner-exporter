package ee.moo.miner.exporter.miner.whatsminer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Whatsminer implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    @Override
    public MinerConfig getConfig() {
        return config;
    }

    @Override
    public MinerType getType() {
        return MinerType.WHATSMINER;
    }

    @Override
    public Metrics getMetrics() {
        return Metrics.builder()
            .miner(getId())
            .type(getType())
            .uptime(1)
            .accepted(2)
            .rejected(3)
            .hashrate(1.5)
            .temperature(4.0)
            .build();
    }
}
