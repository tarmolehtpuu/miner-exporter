package ee.moo.miner.exporter.client.cgminer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CGMinerStatsPool extends CGMinerStats {

    @JsonProperty("Pool Calls")
    private Integer poolCalls;

    @JsonProperty("Pool Attempts")
    private Integer poolAttempts;

    @JsonProperty("Pool Wait")
    private Double poolWait;

    @JsonProperty("Pool Max")
    private Double poolMax;

    @JsonProperty("Pool Min")
    private Double poolMin;

    @JsonProperty("Pool Av")
    private Double poolAv;

    @JsonProperty("Work Had Roll Time")
    private boolean workHadRollTime;

    @JsonProperty("Work Can Roll")
    private boolean workCanRoll;

    @JsonProperty("Work Had Expire")
    private boolean workHadExpire;

    @JsonProperty("Work Roll Time")
    private Integer workRollTime;

    @JsonProperty("Work Diff")
    private Double workDiff;

    @JsonProperty("Min Diff")
    private Double minDiff;

    @JsonProperty("Max Diff")
    private Double maxDiff;

    @JsonProperty("Min Diff Count")
    private Integer minDiffCount;

    @JsonProperty("Max Diff Count")
    private Integer maxDiffCount;

    @JsonProperty("Times Sent")
    private Integer timesSent;

    @JsonProperty("Bytes Sent")
    private Integer bytesSent;

    @JsonProperty("Times Recv")
    private Integer timesRecv;

    @JsonProperty("Bytes Recv")
    private Integer bytesRecv;

    @JsonProperty("Net Bytes Sent")
    private Integer netBytesSent;

    @JsonProperty("Net Bytes Recv")
    private Integer netBytesRecv;

    public CGMinerStatsPool(CGMinerStats stats) {
        this.stats = stats.getStats();
        this.id = stats.getId();
        this.elapsed = stats.getElapsed();
        this.calls = stats.getCalls();
        this.wait = stats.getWait();
        this.max = stats.getMax();
        this.min = stats.getMin();

        var fields = stats.getFields();

        if (fields.containsKey("Pool Calls")) {
            this.poolCalls = (Integer) fields.get("Pool Calls");
        }

        if (fields.containsKey("Pool Attempts")) {
            this.poolAttempts = (Integer) fields.get("Pool Attempts");
        }

        if (fields.containsKey("Pool Wait")) {
            this.poolWait = (Double) fields.get("Pool Wait");
        }

        if (fields.containsKey("Pool Max")) {
            this.poolMax = (Double) fields.get("Pool Max");
        }

        if (fields.containsKey("Pool Min")) {
            this.poolMin = (Double) fields.get("Pool Min");
        }

        if (fields.containsKey("Pool Av")) {
            this.poolAv = (Double) fields.get("Pool Av");
        }

        if (fields.containsKey("Work Had Roll Time")) {
            this.workHadRollTime = (Boolean) fields.get("Work Had Roll Time");
        }

        if (fields.containsKey("Work Can Roll")) {
            this.workCanRoll = (Boolean) fields.get("Work Can Roll");
        }

        if (fields.containsKey("Work Had Expire")) {
            this.workHadExpire = (Boolean) fields.get("Work Had Expire");
        }

        if (fields.containsKey("Work Roll Time")) {
            this.workRollTime = (Integer) fields.get("Work Roll Time");
        }

        if (fields.containsKey("Work Diff")) {
            this.workDiff = (Double) fields.get("Work Diff");
        }

        if (fields.containsKey("Min Diff")) {
            this.minDiff = (Double) fields.get("Min Diff");
        }

        if (fields.containsKey("Max Diff")) {
            this.maxDiff = (Double) fields.get("Max Diff");
        }

        if (fields.containsKey("Min Diff Count")) {
            this.minDiffCount = (Integer) fields.get("Min Diff Count");
        }

        if (fields.containsKey("Max Diff Count")) {
            this.maxDiffCount = (Integer) fields.get("Max Diff Count");
        }

        if (fields.containsKey("Times Sent")) {
            this.timesSent = (Integer) fields.get("Times Sent");
        }

        if (fields.containsKey("Bytes Sent")) {
            this.bytesSent = (Integer) fields.get("Bytes Sent");
        }

        if (fields.containsKey("Times Recv")) {
            this.timesRecv = (Integer) fields.get("Times Recv");
        }

        if (fields.containsKey("Bytes Recv")) {
            this.bytesRecv = (Integer) fields.get("Bytes Recv");
        }

        if (fields.containsKey("Net Bytes Sent")) {
            this.netBytesSent = (Integer) fields.get("Net Bytes Sent");
        }

        if (fields.containsKey("Net Bytes Recv")) {
            this.netBytesRecv = (Integer) fields.get("Net Bytes Recv");
        }
    }
}
