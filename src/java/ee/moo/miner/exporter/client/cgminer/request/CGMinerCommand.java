package ee.moo.miner.exporter.client.cgminer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CGMinerCommand {

    @JsonProperty
    private String command;
}
