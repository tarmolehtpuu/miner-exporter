/*
   miner-exporter - Prometheus exporter for cryptocurrency miners
   Copyright 2026 Tarmo Lehtpuu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ee.moo.miner.exporter.miner;

import ee.moo.miner.exporter.prometheus.*;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static ee.moo.miner.exporter.prometheus.Metric.counter;
import static ee.moo.miner.exporter.prometheus.Metric.gauge;

@Data
public class MinerMetrics {


    public final static Metric MINER_UPTIME_TOTAL = counter(
        "miner_uptime_total",
        "Time since last boot in seconds"
    );

    public final static Metric MINER_ACCEPTED_TOTAL = counter(
        "miner_accepted_total",
        "Total number of shares accepted"
    );

    public final static Metric MINER_REJECTED_TOTAL = counter(
        "miner_rejected_total",
        "Total number of shares rejected"
    );

    public final static Metric MINER_FOUND_TOTAL = counter(
        "miner_found_total",
        "Total number of blocks found"
    );

    public final static Metric MINER_HASHRATE = gauge(
        "miner_hashrate",
        "Current miner hashrate in TH/s"
    );

    public final static Metric MINER_TEMPERATURE = gauge(
        "miner_temperature",
        "Current miner temperature in C"
    );

    public final static Metric MINER_FAN_RPM = gauge(
        "miner_fan_rpm",
        "Current fan RPM"
    );

    public final static Metric MINER_POOL_ALIVE = gauge(
        "miner_pool_alive",
        "Current pool liveness"
    );

    public final static Metric MINER_POOL_ACTIVE = gauge(
        "miner_pool_active",
        "If pool is currently active"
    );

    public final static Metric MINER_POOL_ACCEPTED_TOTAL = counter(
        "miner_pool_accepted_total",
        "Total number of shares accepted for the pool"
    );

    public final static Metric MINER_POOL_REJECTED_TOTAL = counter(
        "miner_pool_rejected_total",
        "Total number of shares rejected for the pool"
    );

    private String miner;

    private MinerType type;

    private Integer uptime;

    private Integer accepted;

    private Integer rejected;

    private Integer found;

    private Double hashrate;

    private List<Temperature> temperatures;

    private List<Fan> fans;

    private List<Pool> pools;

    public void setHashrateMhs(double hashrate) {
        this.hashrate = Math.round(hashrate / 1_000_000.0 * 1000.0) / 1000.0;
    }

    public void setHashrateGhs(double hashrate) {
        this.hashrate = Math.round(hashrate / 1000.0 * 1000.0) / 1000.0;
    }

    public String export() {
        var labels = List.of(
            new Label("miner", getMiner()),
            new Label("miner_type", getType())
        );

        var items = new ArrayList<MetricWithSamples>();

        items.add(new MetricWithSamples(MINER_UPTIME_TOTAL, new Sample(labels, getUptime())));
        items.add(new MetricWithSamples(MINER_ACCEPTED_TOTAL, new Sample(labels, getAccepted())));
        items.add(new MetricWithSamples(MINER_REJECTED_TOTAL, new Sample(labels, getRejected())));
        items.add(new MetricWithSamples(MINER_FOUND_TOTAL, new Sample(labels, getFound())));
        items.add(new MetricWithSamples(MINER_HASHRATE, new Sample(labels, getHashrate())));

        items.add(new MetricWithSamples(
            MINER_TEMPERATURE,
            getTemperatures()
                .stream()
                .map(t -> new Sample(labels, t.getVars(), t.getValue()))
                .toList()
        ));

        items.add(new MetricWithSamples(
            MINER_FAN_RPM,
            getFans()
                .stream()
                .map(f -> new Sample(labels, f.getVars(), f.getValue()))
                .toList()
        ));

        items.add(new MetricWithSamples(
            MINER_POOL_ALIVE,
            getPools()
                .stream()
                .map(p -> new Sample(labels, p.getVars(), p.isAlive()))
                .toList()
        ));

        items.add(new MetricWithSamples(
            MINER_POOL_ACTIVE,
            getPools()
                .stream()
                .map(p -> new Sample(labels, p.getVars(), p.isActive()))
                .toList()
        ));

        items.add(new MetricWithSamples(
            MINER_POOL_ACCEPTED_TOTAL,
            getPools()
                .stream()
                .map(p -> new Sample(labels, p.getVars(), p.getAccepted()))
                .toList()
        ));

        items.add(new MetricWithSamples(
            MINER_POOL_REJECTED_TOTAL,
            getPools()
                .stream()
                .map(p -> new Sample(labels, p.getVars(), p.getRejected()))
                .toList()
        ));

        return new Exporter(items).export();
    }

    @Data
    @Builder
    public static class Fan {

        private Integer id;

        private Integer value;

        public List<Label> getVars() {
            return List.of(new Label("fan", id));
        }
    }

    @Data
    @Builder
    public static class Pool {

        private Integer id;

        private String uri;

        private String user;

        private Integer priority;

        private boolean alive;

        private boolean active;

        private Integer accepted;

        private Integer rejected;

        public List<Label> getVars() {
            return List.of(
                new Label("pool", getId()),
                new Label("pool_priority", getPriority()),
                new Label("pool_uri", getUri()),
                new Label("pool_user", getUser())
            );
        }
    }

    @Data
    @Builder
    public static class Temperature {

        private Integer id;

        private Type type;

        private Double value;

        public List<Label> getVars() {
            return List.of(
                new Label("board", getId()),
                new Label("temperature_type", getType())
            );
        }

        public enum Type {
            CHIP,
            PCB,
        }
    }
}
