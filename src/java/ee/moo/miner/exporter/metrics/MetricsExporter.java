package ee.moo.miner.exporter.metrics;

import ee.moo.miner.exporter.miner.MinerType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MetricsExporter {

    private final static Header MINER_UPTIME_TOTAL = counter(
        "miner_uptime_total",
        "Time since last boot in seconds"
    );

    private final static Header MINER_ACCEPTED_TOTAL = counter(
        "miner_accepted_total",
        "Total number of shares accepted"
    );

    private final static Header MINER_REJECTED_TOTAL = counter(
        "miner_rejected_total",
        "Total number of shares rejected"
    );

    private final static Header MINER_FOUND_TOTAL = counter(
        "miner_found_total",
        "Total number of blocks found"
    );

    private final static Header MINER_HASHRATE = gauge(
        "miner_hashrate",
        "Current miner hashrate in TH/s"
    );

    private final static Header MINER_TEMPERATURE = gauge(
        "miner_temperature",
        "Current miner temperature in C"
    );

    private final static Header MINER_FAN_RPM = gauge(
        "miner_fan_rpm",
        "Current fan RPM"
    );

    private final static Header MINER_POOL_ALIVE = gauge(
        "miner_pool_alive",
        "Current pool liveness"
    );

    private final static Header MINER_POOL_ACTIVE = gauge(
        "miner_pool_active",
        "If pool is currently active"
    );

    private final static Header MINER_POOL_ACCEPTED_TOTAL = counter(
        "miner_pool_accepted_total",
        "Total number of shares accepted for the pool"
    );

    private final static Header MINER_POOL_REJECTED_TOTAL = counter(
        "miner_pool_rejected_total",
        "Total number of shares rejected for the pool"
    );

    private final Metrics metrics;

    private final List<Label> labels;

    public MetricsExporter(Metrics metrics) {
        this.metrics = metrics;
        this.labels = List.of();
    }

    public String export() {
        var sb = new StringBuilder();

        write(sb, MINER_UPTIME_TOTAL, metrics.getUptime());
        write(sb, MINER_ACCEPTED_TOTAL, metrics.getAccepted());
        write(sb, MINER_REJECTED_TOTAL, metrics.getRejected());
        write(sb, MINER_FOUND_TOTAL, metrics.getFound());
        write(sb, MINER_HASHRATE, metrics.getHashrate());

        for (var temp : metrics.getTemperature()) {
            var vars = List.of(
                new Label("board", temp.getNo()),
                new Label("temperature_type", temp.getType().toString())
            );
            write(sb, MINER_TEMPERATURE, vars, temp.getValue());
        }

        for (var fan : metrics.getFan()) {
            write(sb, MINER_FAN_RPM, new Label("fan", fan.getNo()), fan.getValue());
        }

        for (var pool : metrics.getPool()) {
            var vars = List.of(
                new Label("pool", pool.getNo()),
                new Label("pool_priority", pool.getPriority()),
                new Label("pool_uri", pool.getUri()),
                new Label("pool_user", pool.getUser())
            );
            write(sb, MINER_POOL_ALIVE, vars, pool.isAlive() ? 1 : 0);
        }
        for (var pool : metrics.getPool()) {
            var vars = List.of(
                new Label("pool", pool.getNo()),
                new Label("pool_priority", pool.getPriority()),
                new Label("pool_uri", pool.getUri()),
                new Label("pool_user", pool.getUser())
            );
            write(sb, MINER_POOL_ACTIVE, vars, pool.isActive() ? 1 : 0);
        }
        for (var pool : metrics.getPool()) {
            var vars = List.of(
                new Label("pool", pool.getNo()),
                new Label("pool_priority", pool.getPriority()),
                new Label("pool_uri", pool.getUri()),
                new Label("pool_user", pool.getUser())
            );
            write(sb, MINER_POOL_ACCEPTED_TOTAL, vars, pool.getAccepted());
        }
        for (var pool : metrics.getPool()) {
            var vars = List.of(
                new Label("pool", pool.getNo()),
                new Label("pool_priority", pool.getPriority()),
                new Label("pool_uri", pool.getUri()),
                new Label("pool_user", pool.getUser())
            );
            write(sb, MINER_POOL_REJECTED_TOTAL, vars, pool.getRejected());
        }

        return sb.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private void write(StringBuilder sb, Header header, Label var, double value) {
        write(sb, header, labels, List.of(var), value);
    }

    private void write(StringBuilder sb, Header header, List<Label> vars, double value) {
        write(sb, header, labels, vars, value);
    }

    private void write(StringBuilder sb, Header header, double value) {
        write(sb, header, labels, List.of(), value);
    }

    private void write(StringBuilder sb, Header header, List<Label> consts, List<Label> vars, double value) {
        if (sb.indexOf(String.format("# HELP %s", header.name)) == -1) {
            sb.append(header);
        }

        sb.append(header.name);
        sb.append('{');
        for (var c : consts) {
            sb.append(c);
            sb.append(',');
        }
        for (var v : vars) {
            sb.append(v);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');

        sb.append(' ');
        sb.append(value);
        sb.append('\n');
    }

    public static Header counter(String name, String help) {
        return new Header(name, Type.COUNTER, help);
    }

    public static Header gauge(String name, String help) {
        return new Header(name, Type.GAUGE, help);
    }

    public enum Type {
        GAUGE("gauge"),
        COUNTER("counter");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public record Header(String name, Type type, String help) {
        @Override
        public @NonNull String toString() {
            var sb = new StringBuilder();
            sb.append("# HELP ")
                .append(name)
                .append(' ')
                .append(help)
                .append('\n');
            sb.append("# TYPE ")
                .append(name)
                .append(' ')
                .append(type)
                .append('\n');

            return sb.toString();
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public record Label(String name, String value) {

        public Label(String name, MinerType type) {
            this(name, String.valueOf(type));
        }

        public Label(String name, int value) {
            this(name, String.valueOf(value));
        }

        @Override
        public @NonNull String toString() {
            var sb = new StringBuilder();
            sb.append(name);
            sb.append('=');
            sb.append('"');
            sb.append(value);
            sb.append('"');
            return sb.toString();
        }
    }
}
