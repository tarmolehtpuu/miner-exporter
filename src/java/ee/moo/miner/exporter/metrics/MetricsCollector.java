package ee.moo.miner.exporter.metrics;

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

        uptime(mfs);
        accepted(mfs);
        rejected(mfs);
        found(mfs);
        hashrate(mfs);
        temperature(mfs);
        fan(mfs);
        pool(mfs);

        return mfs;
    }

    private void uptime(List<MetricFamilySamples> mfs) {
        var sample = new Collector.MetricFamilySamples.Sample(
            "miner_uptime",
            List.of("miner", "type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            metrics.getUptime()
        );

        mfs.add(new Collector.MetricFamilySamples("" +
            "miner_uptime",
            Type.COUNTER,
            "Time since last boot in seconds",
            List.of(sample)
        ));
    }

    private void accepted(List<MetricFamilySamples> mfs) {
        var sample = new Collector.MetricFamilySamples.Sample(
            "miner_accepted_total",
            List.of("miner", "miner_type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            metrics.getAccepted()
        );

        mfs.add(new Collector.MetricFamilySamples(
            "miner_accepted_total",
            Type.COUNTER,
            "Total number of shares accepted",
            List.of(sample)
        ));
    }

    private void rejected(List<MetricFamilySamples> mfs) {
        var sample = new Collector.MetricFamilySamples.Sample(
            "miner_rejected_total",
            List.of("miner", "miner_type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            metrics.getRejected()
        );

        mfs.add(new Collector.MetricFamilySamples(
            "miner_rejected_total",
            Type.COUNTER,
            "Total number of shares rejected",
            List.of(sample)
        ));
    }

    private void found(List<MetricFamilySamples> mfs) {
        var sample = new Collector.MetricFamilySamples.Sample(
            "miner_found_total",
            List.of("miner", "miner_type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            metrics.getFound()
        );

        mfs.add(new Collector.MetricFamilySamples(
            "miner_found_total",
            Type.COUNTER,
            "Total number of blocks found",
            List.of(sample)
        ));
    }

    private void hashrate(List<MetricFamilySamples> mfs) {
        var sample = new Collector.MetricFamilySamples.Sample(
            "miner_hashrate",
            List.of("miner", "miner_type"),
            List.of(metrics.getMiner(), metrics.getType().toString()),
            metrics.getHashrate()
        );

        mfs.add(new Collector.MetricFamilySamples(
            "miner_hashrate",
            Type.GAUGE,
            "Current miner hashrate",
            List.of(sample)
        ));
    }

    private void temperature(List<MetricFamilySamples> mfs) {
        var samples = new ArrayList<MetricFamilySamples.Sample>();

        for (var t : metrics.getTemperature()) {
            samples.add(new Collector.MetricFamilySamples.Sample(
                "miner_temperature",
                List.of("miner", "miner_type", "board_no", "temperature_type"),
                List.of(metrics.getMiner(), metrics.getType().toString(), String.valueOf(t.getNo()), t.getType().toString()),
                t.getValue()
            ));
        }

        mfs.add(new MetricFamilySamples(
            "miner_temperature",
            Type.GAUGE,
            "Current miner temperature",
            samples
        ));
    }

    private void fan(List<MetricFamilySamples> mfs) {
        var samples = new ArrayList<MetricFamilySamples.Sample>();

        for (var rpm : metrics.getFan()) {
            samples.add(new Collector.MetricFamilySamples.Sample(
                "miner_fan_rpm",
                List.of("miner", "miner_type", "fan_no"),
                List.of(metrics.getMiner(), metrics.getType().toString(), String.valueOf(rpm.getNo())),
                rpm.getValue()
            ));
        }

        mfs.add(new MetricFamilySamples(
            "miner_fan_rpm",
            Type.GAUGE,
            "Current fan RPM",
            samples
        ));
    }

    private void pool(List<MetricFamilySamples> mfs) {
        var samples1 = new ArrayList<MetricFamilySamples.Sample>();
        var samples2 = new ArrayList<MetricFamilySamples.Sample>();
        var samples3 = new ArrayList<MetricFamilySamples.Sample>();
        var samples4 = new ArrayList<MetricFamilySamples.Sample>();

        for (var pool : metrics.getPool()) {
            samples1.add(new Collector.MetricFamilySamples.Sample(
                "miner_pool_alive",
                List.of(
                    "miner",
                    "miner_type",
                    "pool_no",
                    "pool_priority",
                    "pool_uri",
                    "pool_user"
                ),
                List.of(
                    metrics.getMiner(),
                    metrics.getType().toString(),
                    String.valueOf(pool.getNo()),
                    String.valueOf(pool.getPriority()),
                    pool.getUri(),
                    pool.getUser()
                ),
                pool.isActive() ? 1 : 0
            ));
            samples2.add(new Collector.MetricFamilySamples.Sample(
                "miner_pool_active",
                List.of(
                    "miner",
                    "miner_type",
                    "pool_no",
                    "pool_priority",
                    "pool_uri",
                    "pool_user"
                ),
                List.of(
                    metrics.getMiner(),
                    metrics.getType().toString(),
                    String.valueOf(pool.getNo()),
                    String.valueOf(pool.getPriority()),
                    pool.getUri(),
                    pool.getUser()
                ),
                pool.isAlive() ? 1 : 0
            ));
            samples3.add(new Collector.MetricFamilySamples.Sample(
                "miner_pool_accepted_total",
                List.of(
                    "miner",
                    "miner_type",
                    "pool_no",
                    "pool_priority",
                    "pool_uri",
                    "pool_user"
                ),
                List.of(
                    metrics.getMiner(),
                    metrics.getType().toString(),
                    String.valueOf(pool.getNo()),
                    String.valueOf(pool.getPriority()),
                    pool.getUri(),
                    pool.getUser()
                ),
                pool.getAccepted()
            ));
            samples4.add(new Collector.MetricFamilySamples.Sample(
                "miner_pool_rejected_total",
                List.of(
                    "miner",
                    "miner_type",
                    "pool_no",
                    "pool_priority",
                    "pool_uri",
                    "pool_user"
                ),
                List.of(
                    metrics.getMiner(),
                    metrics.getType().toString(),
                    String.valueOf(pool.getNo()),
                    String.valueOf(pool.getPriority()),
                    pool.getUri(),
                    pool.getUser()
                ),
                pool.getRejected()
            ));
        }

        mfs.add(new MetricFamilySamples(
            "miner_pool_alive",
            Type.GAUGE,
            "Current pool liveness",
            samples1
        ));
        mfs.add(new MetricFamilySamples(
            "miner_pool_active",
            Type.GAUGE,
            "If pool is currently active",
            samples2
        ));
        mfs.add(new MetricFamilySamples(
            "miner_pool_accepted_total",
            Type.COUNTER,
            "Number of shares accepted for the pool",
            samples3
        ));
        mfs.add(new MetricFamilySamples(
            "miner_pool_rejected_total",
            Type.COUNTER,
            "Number of shared rejected for the pool",
            samples4
        ));
    }
}
