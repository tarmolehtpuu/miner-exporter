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

import ee.moo.tiny.prometheus.*;

import java.util.ArrayList;
import java.util.List;

import static ee.moo.tiny.prometheus.Metric.counter;
import static ee.moo.tiny.prometheus.Metric.gauge;

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
            new Label("type", getType())
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
                .map(t -> new Sample(labels, t.getVars(), t.value()))
                .toList()
        ));

        items.add(new MetricWithSamples(
            MINER_FAN_RPM,
            getFans()
                .stream()
                .map(f -> new Sample(labels, f.getVars(), f.value()))
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

        return new PrometheusExporter(items).export();
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public MinerType getType() {
        return type;
    }

    public void setType(MinerType type) {
        this.type = type;
    }

    public Integer getUptime() {
        return uptime;
    }

    public void setUptime(Integer uptime) {
        this.uptime = uptime;
    }

    public Integer getAccepted() {
        return accepted;
    }

    public void setAccepted(Integer accepted) {
        this.accepted = accepted;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Integer getFound() {
        return found;
    }

    public void setFound(Integer found) {
        this.found = found;
    }

    public Double getHashrate() {
        return hashrate;
    }

    public void setHashrate(Double hashrate) {
        this.hashrate = hashrate;
    }

    public List<Temperature> getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(List<Temperature> temperatures) {
        this.temperatures = temperatures;
    }

    public List<Fan> getFans() {
        return fans;
    }

    public void setFans(List<Fan> fans) {
        this.fans = fans;
    }

    public List<Pool> getPools() {
        return pools;
    }

    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    public record Fan(Integer id, Integer value) {

        public List<Label> getVars() {
            return List.of(new Label("fan", id));
        }
    }

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

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public void setUri(String protocol, String host, int port) {
            this.uri = String.format("%s://%s:%d", protocol, host, port);
        }

        public void setStratumUri(String host, int port) {
            setUri("stratum+tcp", host, port);
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public boolean isAlive() {
            return alive;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Integer getAccepted() {
            return accepted;
        }

        public void setAccepted(Integer accepted) {
            this.accepted = accepted;
        }

        public Integer getRejected() {
            return rejected;
        }

        public void setRejected(Integer rejected) {
            this.rejected = rejected;
        }
    }

    public record Temperature(Integer id, Type type, Double value) {

        public List<Label> getVars() {
            return List.of(
                new Label("board", id),
                new Label("temperature_type", type)
            );
        }


        public enum Type {
            CHIP,
            PCB
        }
    }
}
