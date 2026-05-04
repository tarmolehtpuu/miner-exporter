package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerSummary;
import lombok.Data;

import java.util.List;

@Data
public class CGMinerSummaryResponse {

    @JsonProperty
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @JsonProperty("SUMMARY")
    private List<CGMinerSummary> summary;

    public boolean isError() {
        if (status.isEmpty()) {
            return true;
        }

        return !status.getFirst().getStatus().equals("S");
    }

    public String getError() {
        if (!isError()) {
            return "";
        }

        if (status.isEmpty()) {
            return "Unknown error";
        }

        return status.getFirst().getMessage();
    }

    public CGMinerSummary toSummary() {
        if (isError()) {
            return null;
        }

        if (summary.isEmpty()) {
            return null;
        }

        return summary.getFirst();
    }
}
