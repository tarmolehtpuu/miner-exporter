package ee.moo.miner.exporter.miner.antminer.model;

import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Builder
@Data
public class AntMinerPool {

    private Integer id;

    private URI uri;

    private String user;

    private String status;

    private Integer priority;
}
