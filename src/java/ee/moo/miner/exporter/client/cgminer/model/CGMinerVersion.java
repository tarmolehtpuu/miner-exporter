package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CGMinerVersion {

    @JsonProperty("CGMiner")
    private String cgminer;

    @JsonProperty("API")
    private String api;

    @JsonProperty("PROD")
    private String product;

    @JsonProperty("MODEL")
    private String model;

    @JsonProperty("HWTYPE")
    private String hwtype;

    @JsonProperty("SWTYPE")
    private String swtype;

    @JsonProperty("VERSION")
    private String version;

    @JsonProperty("HVERSION")
    private String hversion;

    @JsonProperty("UPAPI")
    private String upapi;
}
