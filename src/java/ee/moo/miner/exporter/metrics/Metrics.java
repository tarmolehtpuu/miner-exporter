package ee.moo.miner.exporter.metrics;

import ee.moo.miner.exporter.miner.MinerType;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Data
@Builder
public class Metrics {

    private String miner;

    private MinerType type;

    private Integer uptime;

    private Integer accepted;

    private Integer rejected;

    private Integer found;

    private Double hashrate;

    private List<MetricsTemperature> temperature;

    private List<MetricsFan> fan;

    private List<MetricsPool> pool;

    public String export() {
        CollectorRegistry registry = new CollectorRegistry();

        new MetricsCollector(this)
            .register(registry);

        var writer = new StringWriter();
        try {
            TextFormat.write004(writer, registry.metricFamilySamples());
        } catch (IOException e) {
            throw new MetricsException(e.getMessage(), e);
        }

        return writer.toString();
    }
}
