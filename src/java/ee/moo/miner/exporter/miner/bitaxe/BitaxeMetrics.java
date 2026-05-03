package ee.moo.miner.exporter.miner.bitaxe;

import ee.moo.miner.exporter.metrics.Metrics;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BitaxeMetrics implements Metrics {

    private final BitaxeInfo info;

    @Override
    public Double getVoltage() {
        return info.getVoltage();
    }

    @Override
    public Double getCurrent() {
        return info.getCurrent();
    }

    @Override
    public Double getPower() {
        return info.getPower();
    }
}
