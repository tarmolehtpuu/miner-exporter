package ee.moo.miner.exporter.metrics.model;

import java.util.List;

public record Sample(List<Label> labels, List<Label> vars, Double value) {

    public Sample(Double value) {
        this(List.of(), List.of(), value);
    }

    public Sample(Integer value) {
        this(List.of(), List.of(), value.doubleValue());
    }

    public Sample(Boolean value) {
        this(List.of(), List.of(), value ? 1.0 : 0.0);
    }

    public Sample(List<Label> labels, Double value) {
        this(labels, List.of(), value);
    }

    public Sample(List<Label> labels, Integer value) {
        this(labels, List.of(), value.doubleValue());
    }

    public Sample(List<Label> labels, Boolean value) {
        this(labels, List.of(), value ? 1.0 : 0.0);
    }

    public Sample(List<Label> labels, List<Label> vars, Integer value) {
        this(labels, vars, value.doubleValue());
    }

    public Sample(List<Label> labels, List<Label> vars, Boolean value) {
        this(labels, vars, value ? 1.0 : 0.0);
    }
}
