package ee.moo.miner.exporter.miner.avalon;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AvalonNano3 implements Miner {

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
        return new AvalonNano3Metrics();
    }
}
