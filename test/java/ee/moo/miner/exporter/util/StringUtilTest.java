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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilTest {

    @Test
    public void testEquals() {
        assertFalse(StringUtil.equals("foo", "bar"));
        assertFalse(StringUtil.equals(null, ""));
        assertFalse(StringUtil.equals("", null));
        assertFalse(StringUtil.equals("foo", "FOO"));
        assertTrue(StringUtil.equals("foo", "foo"));
        assertTrue(StringUtil.equals("", ""));
        assertTrue(StringUtil.equals(null, null));
    }

    @Test
    public void testEqualsIgnoreCase() {
        assertFalse(StringUtil.equalsIgnoreCase("foo", "bar"));
        assertFalse(StringUtil.equalsIgnoreCase(null, ""));
        assertFalse(StringUtil.equalsIgnoreCase("", null));
        assertTrue(StringUtil.equalsIgnoreCase("foo", "FOO"));
        assertTrue(StringUtil.equalsIgnoreCase("foo", "foo"));
        assertTrue(StringUtil.equalsIgnoreCase("", ""));
        assertTrue(StringUtil.equalsIgnoreCase(null, null));
    }

    @SuppressWarnings("ConstantValue")
    @Test
    public void testIsEmpty() {
        assertFalse(StringUtil.isEmpty("foo"));
        assertFalse(StringUtil.isEmpty("BAR"));
        assertTrue(StringUtil.isEmpty(""));
        assertTrue(StringUtil.isEmpty(null));
    }
}
