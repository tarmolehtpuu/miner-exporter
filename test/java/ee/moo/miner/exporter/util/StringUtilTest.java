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
