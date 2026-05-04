package ee.moo.miner.exporter.miner.nano3;

import ee.moo.miner.exporter.metrics.Metrics;

public class Nano3Metrics implements Metrics {

    @Override
    public Double getVoltage() {
        return 0.0;
    }

    @Override
    public Double getCurrent() {
        return 0.0;
    }

    @Override
    public Double getPower() {
        return 0.0;
    }
}
