package ee.moo.miner.exporter.miner;

import ee.moo.miner.exporter.metrics.Metrics;

import java.net.URI;

public interface Miner {

    MinerConfig getConfig();

    MinerType getType();

    Metrics getMetrics();

    default String getId() {
        return getConfig().getId();
    }

    default URI getUri() {
        return getConfig().getUri();
    }
}
