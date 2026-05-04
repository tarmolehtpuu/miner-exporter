package ee.moo.miner.exporter.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class CGMinerStatus {

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("When")
    private Instant time;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Msg")
    private String message;

    @JsonProperty("Description")
    private String description;


}
