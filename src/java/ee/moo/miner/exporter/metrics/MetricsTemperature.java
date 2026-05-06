package ee.moo.miner.exporter.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricsTemperature {

    private Integer no;

    private MetricsTemperatureType type;

    private Double value;
}
