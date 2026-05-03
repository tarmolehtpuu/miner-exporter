package ee.moo.miner.exporter.miner;

import ee.moo.miner.exporter.metrics.Metrics;

public interface Miner {

    MinerConfig getConfig();

    MinerType getType();

    Metrics getMetrics();
    
    default String getId() {
        return getConfig().getId();
    }

    default String getHost() {
        return getConfig().getHost();
    }

    default int getPort() {
        return getConfig().getPort();
    }
}
