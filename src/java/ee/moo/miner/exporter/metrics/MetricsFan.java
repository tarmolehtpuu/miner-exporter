package ee.moo.miner.exporter.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricsFan {

    private Integer no;

    private Integer value;
}
