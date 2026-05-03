package ee.moo.miner.exporter.miner;

public class MinerException extends RuntimeException {

    public MinerException(String message) {
        super(message);
    }

    public MinerException(String message, Object... args) {
        super(String.format(message, args));
    }

    public MinerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinerException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }
}
