package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerSummary;
import lombok.Getter;

import java.util.List;

public class CGMinerSummaryResponseOld implements CGMinerResponseOld {

    @JsonProperty
    @Getter
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @JsonProperty("SUMMARY")
    private List<CGMinerSummary> summary;

    @Override
    public CGMinerStatus getStatus() {
        return status.isEmpty() ? null : status.getFirst();
    }

    public CGMinerSummary getSummary() {
        if (isError()) {
            return null;
        }

        if (summary.isEmpty()) {
            return null;
        }

        return summary.getFirst();
    }
}
