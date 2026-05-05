package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class CGMinerDevice {

    @JsonProperty("ASC")
    private Integer asc;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("Enabled")
    private String enabled;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Temperature")
    private Double temperature;

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

    @JsonProperty("Accepted")
    private Integer accepted;

    @JsonProperty("Rejected")
    private Integer rejected;

    @JsonProperty("Hardware Errors")
    private Integer hardwareErrors;

    @JsonProperty("Utility")
    private Double utility;

    @JsonProperty("Last Share Pool")
    private Integer lastSharePool;

    @JsonProperty("Last Share Time")
    private Instant lastShareTime;

    @JsonProperty("Total MH")
    private Double totalMh;

    @JsonProperty("Diff1 Work")
    private Integer diff1Work;

    @JsonProperty("Difficulty Accepted")
    private Double difficultyAccepted;

    @JsonProperty("Diffculty Rejected")
    private Double difficultyRejected;

    @JsonProperty("Last Share Difficulty")
    private Double lastShareDifficulty;

    @JsonProperty("Last Valid Work")
    private Integer lastWalidWork;

    @JsonProperty("Device Hardware%")
    private Double deviceHarwarePercentage;

    @JsonProperty("Device Rejected%")
    private Double deviceRejectedPercentage;

    @JsonProperty("Device Elapsed")
    private Integer deviceElapsed;
}
