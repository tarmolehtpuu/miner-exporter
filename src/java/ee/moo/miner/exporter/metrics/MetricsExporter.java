package ee.moo.miner.exporter.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.StringWriter;

@RequiredArgsConstructor
public class MetricsExporter {

    private final Metrics metrics;

    public String toString() {
        CollectorRegistry registry = new CollectorRegistry();

        Gauge.build()
            .name("miner_voltage")
            .help("Miner Voltage")
            .register(registry)
            .set(metrics.getVoltage());

        Gauge.build()
            .name("miner_current")
            .help("Miner Current")
            .register(registry)
            .set(metrics.getCurrent());

        Gauge.build()
            .name("miner_power")
            .help("Miner Power")
            .register(registry)
            .set(metrics.getPower());

        var writer = new StringWriter();

        try {
            TextFormat.write004(writer, registry.metricFamilySamples());
        } catch (IOException e) {
            throw new MetricsException(e.getMessage(), e);
        }

        return writer.toString();
    }
}
