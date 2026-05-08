package ee.moo.miner.exporter.prometheus;

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
