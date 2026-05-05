package ee.moo.miner.exporter.client.cgminer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerConfig;
import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;

import java.util.List;

public class CGMinerConfigResponseOld implements CGMinerResponseOld {

    @JsonProperty
    private Integer id;

    @JsonProperty("STATUS")
    private List<CGMinerStatus> status;

    @JsonProperty("CONFIG")
    private List<CGMinerConfig> config;

    @Override
    public CGMinerStatus getStatus() {
        return status.isEmpty() ? null : status.getFirst();
    }

    public CGMinerConfig getConfig() {
        if (isError()) {
            return null;
        }

        if (config.isEmpty()) {
            return null;
        }

        return config.getFirst();
    }
}
