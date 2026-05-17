package ee.moo.miner.exporter.util;

import java.io.OutputStream;
import java.io.PrintStream;

public class Silence implements AutoCloseable {

    private final OutputStream out;
    private final OutputStream err;

    private Silence(boolean out, boolean err) {
        this.err = System.err;
        this.out = System.out;

        if (out) {
            System.setOut(new PrintStream(PrintStream.nullOutputStream()));
        }
        if (err) {
            System.setErr(new PrintStream(PrintStream.nullOutputStream()));
        }
    }

    @Override
    public void close() {
        System.setErr(new PrintStream(err));
        System.setOut(new PrintStream(out));
    }

    public static Silence err() {
        return new Silence(false, true);
    }

    public static Silence out() {
        return new Silence(true, false);
    }

    public static Silence both() {
        return new Silence(true, true);
    }
}
