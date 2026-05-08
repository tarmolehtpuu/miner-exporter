package ee.moo.miner.exporter.metrics;

import ee.moo.miner.exporter.metrics.model.Label;
import ee.moo.miner.exporter.metrics.model.Metric;
import ee.moo.miner.exporter.metrics.model.Sample;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static ee.moo.miner.exporter.metrics.model.Metric.*;

@RequiredArgsConstructor
public class MetricsExporter {

    private final Metrics metrics;

    private final List<Label> labels;

    public String export() {
        var sb = new StringBuilder();

        write(sb, MINER_UPTIME_TOTAL, new Sample(labels, metrics.getUptime()));
        write(sb, MINER_ACCEPTED_TOTAL, new Sample(labels, metrics.getAccepted()));
        write(sb, MINER_REJECTED_TOTAL, new Sample(labels, metrics.getRejected()));
        write(sb, MINER_FOUND_TOTAL, new Sample(labels, metrics.getFound()));
        write(sb, MINER_HASHRATE, new Sample(labels, metrics.getHashrate()));

        for (var t : metrics.getTemperatures()) {
            write(sb, MINER_TEMPERATURE, new Sample(labels, t.getVars(), t.getValue()));
        }

        for (var f : metrics.getFans()) {
            write(sb, MINER_FAN_RPM, new Sample(labels, f.getVars(), f.getValue()));
        }

        var samples1 = new ArrayList<Sample>();
        var samples2 = new ArrayList<Sample>();
        var samples3 = new ArrayList<Sample>();
        var samples4 = new ArrayList<Sample>();

        for (var p : metrics.getPools()) {
            samples1.add(new Sample(labels, p.getVars(), p.isAlive()));
            samples2.add(new Sample(labels, p.getVars(), p.isActive()));
            samples3.add(new Sample(labels, p.getVars(), p.getAccepted()));
            samples4.add(new Sample(labels, p.getVars(), p.getRejected()));
        }

        write(sb, MINER_POOL_ALIVE, samples1);
        write(sb, MINER_POOL_ACTIVE, samples2);
        write(sb, MINER_POOL_ACCEPTED_TOTAL, samples3);
        write(sb, MINER_POOL_REJECTED_TOTAL, samples4);

        return sb.toString();
    }

    private void write(StringBuilder sb, Metric metric, List<Sample> samples) {
        for (var s : samples) {
            write(sb, metric, s);
        }
    }

    private void write(StringBuilder sb, Metric metric, Sample sample) {
        write(
            sb,
            metric,
            sample.labels(),
            sample.vars(),
            sample.value()
        );
    }

    private void write(StringBuilder sb, Metric metric, List<Label> labels, List<Label> vars, double value) {
        write(sb, metric);

        sb.append(metric.name());
        sb.append('{');
        for (var l : labels) {
            write(sb, l);
            sb.append(',');
        }
        for (var v : vars) {
            write(sb, v);
            sb.append(',');
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');

        sb.append(' ');
        sb.append(value);
        sb.append('\n');
    }

    private void write(StringBuilder sb, Metric metric) {
        if (sb.indexOf(String.format("# HELP %s", metric.name())) == -1) {
            sb.append("# HELP ")
                .append(metric.name())
                .append(' ')
                .append(metric.help())
                .append('\n');
            sb.append("# TYPE ")
                .append(metric.name())
                .append(' ')
                .append(metric.type())
                .append('\n');
        }
    }

    private void write(StringBuilder sb, Label label) {
        sb.append(label.name());
        sb.append('=');
        sb.append('"');
        sb.append(label.value());
        sb.append('"');
    }
}
