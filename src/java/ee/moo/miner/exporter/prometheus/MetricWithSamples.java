package ee.moo.miner.exporter.prometheus;

import java.util.List;

public record MetricWithSamples(Metric metric, List<Sample> samples) {

    public MetricWithSamples(Metric metric, Sample sample) {
        this(metric, List.of(sample));
    }
}
