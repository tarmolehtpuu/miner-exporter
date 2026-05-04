package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CGMinerSummary {

    @JsonProperty("Elapsed")
    private Integer elapsed;

    @JsonProperty("MHS av")
    private Double mhsAv;

    @JsonProperty("MHS 5s")
    private Double mhs5s;

    @JsonProperty("MHS 1m")
    private Double mhs1m;

    @JsonProperty("MHS 5m")
    private Double mhs5m;

    @JsonProperty("MHS 15m")
    private Double mhs15m;

    @JsonProperty("Found Blocks")
    private Integer foundBlocks;

    @JsonProperty("Getworks")
    private Integer getWorks;

    @JsonProperty("Accepted")
    private Integer accepted;

    @JsonProperty("Rejected")
    private Integer rejected;

    @JsonProperty("Hardware Errors")
    private Integer hardwareErrors;

    @JsonProperty("Utility")
    private Double utility;

    @JsonProperty("Discarded")
    private Integer discarded;

    @JsonProperty("Stale")
    private Integer stale;

    @JsonProperty("Get Failures")
    private Integer getFailures;

    @JsonProperty("Local Work")
    private Integer localWork;

    @JsonProperty("Remote Failures")
    private Integer remoteFailures;

    @JsonProperty("Network Blocks")
    private Integer networkBlocks;

    @JsonProperty("Total MH")
    private Double totalMh;

    @JsonProperty("Work Utility")
    private Double workUtility;

    @JsonProperty("Difficulty Accepted")
    private Double difficultyAccepted;

    @JsonProperty("Difficulty Rejected")
    private Double difficultyRejected;

    @JsonProperty("Difficulty Stale")
    private Double difficultySTale;

    @JsonProperty("Best Share")
    private Integer bestShare;

    @JsonProperty("Device Hardware%")
    private Double deviceHardwarePercentage;

    @JsonProperty("Device Rejected%")
    private Double deviceHardwareRejectedPercentage;

    @JsonProperty("Pool Rejected%")
    private Double poolRejectedPercentage;

    @JsonProperty("Pool Stale%")
    private Double poolStalePercentage;

    @JsonProperty("Last getwork")
    private Integer lastGetWork;
}
