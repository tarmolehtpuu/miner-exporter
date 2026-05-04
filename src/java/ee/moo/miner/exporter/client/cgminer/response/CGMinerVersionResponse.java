package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerVersion;
import lombok.Data;

import java.util.List;

@Data
public class CGMinerVersionResponse {

    @JsonProperty
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @JsonProperty("VERSION")
    private List<CGMinerVersion> version;

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

    public CGMinerVersion toVersion() {
        if (isError()) {
            return null;
        }

        if (version.isEmpty()) {
            return null;
        }

        return version.getFirst();
    }
}
