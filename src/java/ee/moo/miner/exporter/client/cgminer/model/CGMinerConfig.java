package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CGMinerConfig {

    @JsonProperty("ASC Count")
    private Integer ascCount;

    @JsonProperty("PGA Count")
    private Integer pgaCount;

    @JsonProperty("Pool Count")
    private Integer poolCount;

    @JsonProperty("Strategy")
    private String strategy;

    @JsonProperty("Log Interval")
    private Integer logInterval;

    @JsonProperty("Device Code")
    private String deviceCode;

    @JsonProperty("OS")
    private String os;

    @JsonProperty("Hotplug")
    private String hotplug;
}
