package ee.moo.miner.exporter.miner.whatsminer;

import ee.moo.miner.exporter.metrics.Metrics;

public class WhatsminerMetrics implements Metrics {

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
