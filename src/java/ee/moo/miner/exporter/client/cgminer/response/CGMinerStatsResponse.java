package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStats;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatsDevice;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatsPool;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CGMinerStatsResponse implements CGMinerResponse {

    @JsonProperty
    @Getter
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @JsonProperty("STATS")
    private List<CGMinerStats> stats;

    @Override
    public CGMinerStatus getStatus() {
        return status.isEmpty() ? null : status.getFirst();
    }

    public List<CGMinerStats> getStats() {
        var result = new ArrayList<CGMinerStats>();

        for (CGMinerStats s : stats) {
            if (s.getId().contains("POOL")) {
                result.add(new CGMinerStatsPool(s));
            } else {
                result.add(new CGMinerStatsDevice(s));
            }
        }

        return result;
    }
}
