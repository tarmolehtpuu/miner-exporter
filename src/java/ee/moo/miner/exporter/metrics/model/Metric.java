package ee.moo.miner.exporter.metrics.model;

public record Metric(String name, Type type, String help) {

    public final static Metric MINER_UPTIME_TOTAL = counter(
        "miner_uptime_total",
        "Time since last boot in seconds"
    );

    public final static Metric MINER_ACCEPTED_TOTAL = counter(
        "miner_accepted_total",
        "Total number of shares accepted"
    );

    public final static Metric MINER_REJECTED_TOTAL = counter(
        "miner_rejected_total",
        "Total number of shares rejected"
    );

    public final static Metric MINER_FOUND_TOTAL = counter(
        "miner_found_total",
        "Total number of blocks found"
    );

    public final static Metric MINER_HASHRATE = gauge(
        "miner_hashrate",
        "Current miner hashrate in TH/s"
    );

    public final static Metric MINER_TEMPERATURE = gauge(
        "miner_temperature",
        "Current miner temperature in C"
    );

    public final static Metric MINER_FAN_RPM = gauge(
        "miner_fan_rpm",
        "Current fan RPM"
    );

    public final static Metric MINER_POOL_ALIVE = gauge(
        "miner_pool_alive",
        "Current pool liveness"
    );

    public final static Metric MINER_POOL_ACTIVE = gauge(
        "miner_pool_active",
        "If pool is currently active"
    );

    public final static Metric MINER_POOL_ACCEPTED_TOTAL = counter(
        "miner_pool_accepted_total",
        "Total number of shares accepted for the pool"
    );

    public final static Metric MINER_POOL_REJECTED_TOTAL = counter(
        "miner_pool_rejected_total",
        "Total number of shares rejected for the pool"
    );

    public enum Type {
        COUNTER("counter"),
        GAUGE("gauge");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static Metric counter(String name, String help) {
        return new Metric(name, Type.COUNTER, help);
    }

    public static Metric gauge(String name, String help) {
        return new Metric(name, Type.GAUGE, help);
    }
}
