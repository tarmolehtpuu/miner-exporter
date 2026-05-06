package ee.moo.miner.exporter.metrics;

import ee.moo.miner.exporter.miner.Miner;
import io.prometheus.client.Collector;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MetricsCollector extends Collector {

    private final Metrics metrics;

    @Override
    public List<MetricFamilySamples> collect() {
        var mfs = new ArrayList<MetricFamilySamples>();

        mfs.add(counter("miner_uptime", "Time since last boot in seconds", metrics.getUptime()));
        mfs.add(counter("miner_accepted_total", "Total number of shares accepted", metrics.getAccepted()));
        mfs.add(counter("miner_rejected_total", "Total number of shares rejected", metrics.getRejected()));
        mfs.add(gauge("miner_hashrate", "Current miner hashrate", metrics.getHashrate()));
        mfs.add(gauge("miner_temperature", "Current miner temperature", metrics.getTemperature()));

        return mfs;
    }

    public Collector.MetricFamilySamples gauge(String name, String help, double value) {
        var sample = new Collector.MetricFamilySamples.Sample(
            name,
            List.of("miner", "type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            value
        );

        return new Collector.MetricFamilySamples(
            name,
            Type.GAUGE,
            help,
            List.of(sample)
        );
    }

    public Collector.MetricFamilySamples counter(String name, String help, double value) {
        var sample = new Collector.MetricFamilySamples.Sample(
            name,
            List.of("miner", "type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            value
        );

        return new Collector.MetricFamilySamples(name, Collector.Type.COUNTER, help, List.of(sample));
    }
}
