package ee.moo.miner.exporter.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricsPool {

    private Integer no;

    private String uri;

    private String user;

    private Integer priority;

    private boolean alive;

    private boolean active;

    private Integer accepted;

    private Integer rejected;
}
