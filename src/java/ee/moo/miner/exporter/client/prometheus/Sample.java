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

import java.util.List;

public record Sample(List<Label> labels, List<Label> vars, Double value) {

    public Sample(Double value) {
        this(List.of(), List.of(), value);
    }

    public Sample(Integer value) {
        this(List.of(), List.of(), value.doubleValue());
    }

    public Sample(Boolean value) {
        this(List.of(), List.of(), value ? 1.0 : 0.0);
    }

    public Sample(List<Label> labels, Double value) {
        this(labels, List.of(), value);
    }

    public Sample(List<Label> labels, Integer value) {
        this(labels, List.of(), value.doubleValue());
    }

    public Sample(List<Label> labels, Boolean value) {
        this(labels, List.of(), value ? 1.0 : 0.0);
    }

    public Sample(List<Label> labels, List<Label> vars, Integer value) {
        this(labels, vars, value.doubleValue());
    }

    public Sample(List<Label> labels, List<Label> vars, Boolean value) {
        this(labels, vars, value ? 1.0 : 0.0);
    }
}
