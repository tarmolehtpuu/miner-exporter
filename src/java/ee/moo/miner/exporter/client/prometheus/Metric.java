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

public record Metric(String name, Type type, String help) {
    
    public enum Type {
        COUNTER("counter"),
        GAUGE("gauge");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static Metric counter(String name, String help) {
        return new Metric(name, Type.COUNTER, help);
    }

    public static Metric gauge(String name, String help) {
        return new Metric(name, Type.GAUGE, help);
    }
}
