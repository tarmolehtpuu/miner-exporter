package ee.moo.miner.exporter.util;

public class StringUtil {

    @SuppressWarnings("ConstantValue")
    public static boolean equals(String s1, String s2) {
        if (s1 != null) {
            return s1.equals(s2);
        }

        if (s2 != null) {
            return s2.equals(s1);
        }

        return true;
    }

    @SuppressWarnings("ConstantValue")
    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (s1 != null) {
            return s1.equalsIgnoreCase(s2);
        }

        if (s2 != null) {
            return s2.equalsIgnoreCase(s1);
        }

        return true;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
