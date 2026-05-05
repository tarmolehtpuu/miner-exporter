package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CGMinerStats {

    @JsonProperty("STATS")
    protected String stats;

    @JsonProperty("ID")
    protected String id;

    @JsonProperty("Elapsed")
    protected Integer elapsed;

    @JsonProperty("Calls")
    protected Integer calls;

    @JsonProperty("Wait")
    protected Double wait;

    @JsonProperty("Max")
    protected Double max;

    @JsonProperty("Min")
    protected Double min;

    @JsonAnySetter
    protected Map<String, Object> fields = new HashMap<String, Object>();
}
