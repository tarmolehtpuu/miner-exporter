package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CGMinerStatsDevice extends CGMinerStats {

    @JsonProperty("MM ID0")
    private String mmid0;

    @JsonProperty("MM Count")
    private Integer mmCount;

    @JsonProperty("Nonce Mask")
    private Integer nonceMask;

    public CGMinerStatsDevice(CGMinerStats stats) {
        this.stats = stats.getStats();
        this.id = stats.getId();
        this.elapsed = stats.getElapsed();
        this.calls = stats.getCalls();
        this.wait = stats.getWait();
        this.max = stats.getMax();
        this.min = stats.getMin();

        var fields = stats.getFields();

        if (fields.containsKey("MM ID0")) {
            this.mmid0 = String.valueOf(fields.get("MM ID0"));
        }

        if (fields.containsKey("MM Count")) {
            this.mmCount = Integer.parseInt(String.valueOf(fields.get("MM Count")));
        }

        if (fields.containsKey("Nonce Mask")) {
            this.nonceMask = Integer.parseInt(String.valueOf(fields.get("Nonce Mask")));
        }
    }
}
