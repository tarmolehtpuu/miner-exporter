package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerVersion;
import lombok.Getter;

import java.util.List;

public class CGMinerVersionResponseOld implements CGMinerResponseOld {

    @JsonProperty
    @Getter
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @JsonProperty("VERSION")
    private List<CGMinerVersion> version;

    @Override
    public CGMinerStatus getStatus() {
        return status.isEmpty() ? null : status.getFirst();
    }

    public CGMinerVersion getVersion() {
        if (isError()) {
            return null;
        }

        if (version.isEmpty()) {
            return null;
        }

        return version.getFirst();
    }
}
