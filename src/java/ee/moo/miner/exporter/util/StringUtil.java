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
package ee.moo.miner.exporter.util;

public final class StringUtil {

    private StringUtil() {
    }

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

    public static String lpad(String s, char pad, int length) {
        if (s == null) {
            s = "";
        }

        var sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.insert(0, pad);
        }

        return sb.toString();
    }

    public static String rpad(String s, char pad, int length) {
        if (s == null) {
            s = "";
        }

        var sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append(pad);
        }

        return sb.toString();
    }
}
