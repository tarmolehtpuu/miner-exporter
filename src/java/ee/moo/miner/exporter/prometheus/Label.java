package ee.moo.miner.exporter.prometheus;

public record Label(String name, String value) {

    public Label(String name, Integer value) {
        this(name, String.valueOf(value));
    }

    public Label(String name, Object value) {
        this(name, String.valueOf(value));
    }
}
