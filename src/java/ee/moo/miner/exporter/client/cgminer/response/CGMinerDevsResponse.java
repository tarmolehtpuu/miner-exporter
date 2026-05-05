package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerDevice;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import lombok.Getter;

import java.util.List;

public class CGMinerDevsResponse implements CGMinerResponse {

    @JsonProperty
    @Getter
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @Getter
    @JsonProperty("DEVS")
    private List<CGMinerDevice> devices;

    @Override
    public CGMinerStatus getStatus() {
        return status.isEmpty() ? null : status.getFirst();
    }
}
