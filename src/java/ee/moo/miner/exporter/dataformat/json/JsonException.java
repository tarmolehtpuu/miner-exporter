package ee.moo.miner.exporter.dataformat.json;

public class JsonException extends RuntimeException {

    public JsonException(String message, Object... args) {
        super(String.format(message, args));
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }
}