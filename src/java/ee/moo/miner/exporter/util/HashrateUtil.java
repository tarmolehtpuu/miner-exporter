package ee.moo.miner.exporter.util;

public final class HashrateUtil {

    private HashrateUtil() {
    }

    public static double ghs2ths(double ghs) {
        return Math.round(ghs / 1000.0 * 1000.0) / 1000.0;
    }

    public static double mhs2ths(double mhs) {
        return Math.round(mhs / 1_000_000 * 1000.0) / 1000.0;
    }
}
