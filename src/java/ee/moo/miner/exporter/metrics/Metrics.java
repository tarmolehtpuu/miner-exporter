package ee.moo.miner.exporter.metrics;

public interface Metrics {

    Double getVoltage();

    Double getCurrent();

    Double getPower();

    default String export() {
        return new MetricsExporter(this).toString();
    }
}
