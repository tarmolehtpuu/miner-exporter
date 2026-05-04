package ee.moo.miner.exporter.miner;

import ee.moo.miner.exporter.common.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MinerConfigTest extends UnitTest {

    @Test
    public void testCreateFromEnvironment() {
        var env = Map.of(
            "MINER_0_ID", "miner01",
            "MINER_0_TYPE", "BITAXE",
            "MINER_0_HOST", "127.0.0.1",
            "MINER_0_PORT", "1234",
            "MINER_1_ID", "miner02",
            "MINER_1_TYPE", "BITAXE",
            "MINER_1_HOST", "127.0.0.1",
            "MINER_1_PORT", "1235"
        );

        var cfg = MinerConfig.createFromEnvironment(env);

        assertNotNull(cfg);
        assertEquals(2, cfg.size());

        var m1 = cfg.get(0);
        var m2 = cfg.get(1);

        assertEquals("miner01", m1.getId());
        assertEquals("miner02", m2.getId());

        assertEquals(MinerType.BITAXE, m1.getType());
        assertEquals(MinerType.BITAXE, m2.getType());

        assertEquals("127.0.0.1", m1.getHost());
        assertEquals("127.0.0.1", m2.getHost());

        assertEquals(1234, m1.getPort());
        assertEquals(1235, m2.getPort());
    }
}
