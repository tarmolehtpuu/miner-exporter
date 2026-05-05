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
            "MINER_0_URI", "http://127.0.0.1",
            "MINER_1_ID", "miner02",
            "MINER_1_TYPE", "AVALON",
            "MINER_1_URI", "tcp://127.0.0.1:1235"
        );

        var cfg = MinerConfig.createFromEnvironment(env);

        assertNotNull(cfg);
        assertEquals(2, cfg.size());

        var m1 = cfg.get(0);
        var m2 = cfg.get(1);

        assertEquals("miner01", m1.getId());
        assertEquals("miner02", m2.getId());

        assertEquals(MinerType.BITAXE, m1.getType());
        assertEquals(MinerType.AVALON, m2.getType());

        assertEquals("http://127.0.0.1", m1.getUri().toString());
        assertEquals("tcp://127.0.0.1:1235", m2.getUri().toString());
    }
}
