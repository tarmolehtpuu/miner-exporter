package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerPool;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;
import lombok.Getter;

import java.util.List;

public class CGMinerPoolsResponse implements CGMinerResponse {

    @JsonProperty
    @Getter
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @Getter
    @JsonProperty("POOLS")
    private List<CGMinerPool> pools;

    @Override
    public CGMinerStatus getStatus() {
        return status.isEmpty() ? null : status.getFirst();
    }

}
