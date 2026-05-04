package ee.moo.miner.exporter.client;

public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }
}
