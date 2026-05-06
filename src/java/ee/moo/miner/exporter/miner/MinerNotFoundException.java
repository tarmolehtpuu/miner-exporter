package ee.moo.miner.exporter.miner;

public class MinerNotFoundException extends MinerException {

    public MinerNotFoundException(String message) {
        super(message);
    }

    public MinerNotFoundException(String message, Object... args) {
        super(message, args);
    }

    public MinerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinerNotFoundException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }
}
