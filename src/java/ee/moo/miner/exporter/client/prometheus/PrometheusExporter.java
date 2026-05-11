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
package ee.moo.miner.exporter.client.prometheus;

import java.util.ArrayList;
import java.util.List;

public class PrometheusExporter {

    private final List<MetricWithSamples> metrics;

    public PrometheusExporter(List<MetricWithSamples> metrics) {
        this.metrics = new ArrayList<>(metrics);
    }

    public String export() {
        var sb = new StringBuilder();
        for (var item : metrics) {
            write(sb, item.metric(), item.samples());
        }
        return sb.toString();
    }

    private void write(StringBuilder sb, Metric metric, List<Sample> samples) {
        for (var sample : samples) {
            write(sb, metric, sample.labels(), sample.vars(), sample.value());
        }
    }

    private void write(StringBuilder sb, Metric metric, List<Label> labels, List<Label> vars, double value) {
        write(sb, metric);
        write(sb, labels, vars);
        write(sb, value);
    }

    private void write(StringBuilder sb, Metric metric) {
        if (sb.indexOf(String.format("# HELP %s", metric.name())) == -1) {
            sb.append("# HELP ")
                .append(metric.name())
                .append(' ')
                .append(metric.help())
                .append('\n');
            sb.append("# TYPE ")
                .append(metric.name())
                .append(' ')
                .append(metric.type())
                .append('\n');
        }

        sb.append(metric.name());
    }

    private void write(StringBuilder sb, List<Label> labels, List<Label> vars) {
        var all = new ArrayList<Label>();
        all.addAll(labels);
        all.addAll(vars);

        if (!all.isEmpty()) {
            sb.append('{');
            for (var label : all) {
                write(sb, label);
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('}');
        }

        sb.append(' ');
    }

    private void write(StringBuilder sb, Label label) {
        sb.append(label.name());
        sb.append('=');
        sb.append('"');
        sb.append(label.value());
        sb.append('"');
    }

    private void write(StringBuilder sb, double value) {
        sb.append(value);
        sb.append('\n');
    }
}
