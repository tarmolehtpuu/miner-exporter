package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class CGMinerPool {

    @JsonProperty("POOL")
    private String pool;

    @JsonProperty("URL")
    private String url;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Priority")
    private Integer priority;

    @JsonProperty("Quota")
    private Integer quota;

    @JsonProperty("Long Poll")
    private String longPoll;

    @JsonProperty("Getworks")
    private Integer getWorks;

    @JsonProperty("Accepted")
    private Integer accepted;

    @JsonProperty("Rejected")
    private Integer rejected;

    @JsonProperty("Discarded")
    private Integer discarded;

    @JsonProperty("Stale")
    private Integer stale;

    @JsonProperty("Get Failures")
    private Integer getFailures;

    @JsonProperty("Remote Failures")
    private Integer remoteFailures;

    @JsonProperty("User")
    private String user;

    @JsonProperty("Last Share Time")
    private Instant lastShareTime;

    @JsonProperty("Diff1 Shares")
    private Integer diff1Shares;

    @JsonProperty("Proxy Type")
    private String proxyType;

    @JsonProperty("Proxy")
    private String proxy;

    @JsonProperty("Difficulty Accepted")
    private Double difficultyAccepted;

    @JsonProperty("Difficulty Rejected")
    private Double difficultyRejected;

    @JsonProperty("Difficulty Stale")
    private Double difficultyStale;

    @JsonProperty("Last Share Difficulty")
    private Double lastShareDifficulty;

    @JsonProperty("Has Stratum")
    private Boolean hasStratum;

    @JsonProperty("Stratum Active")
    private Boolean stratumActive;

    @JsonProperty("Stratum URL")
    private String stratumUrl;

    @JsonProperty("Stratum Difficulty")
    private Double stratumDifficulty;

    @JsonProperty("Has Vmask")
    private Boolean hasVmask;

    @JsonProperty("has GBT")
    private Boolean hasGbt;

    @JsonProperty("Best Share")
    private Double bestShare;

    @JsonProperty("Pool Rejected%")
    private Double poolRejectedPercentage;

    @JsonProperty("Pool Stale%")
    private Double poolStalePercentage;

    @JsonProperty("Bad Work")
    private Integer badWork;

    @JsonProperty("Current Block Height")
    private Integer currentBlockHeight;

    @JsonProperty("Current Block Width")
    private Integer currentBlockVersion;


}
