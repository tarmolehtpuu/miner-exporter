package ee.moo.miner.exporter.miner;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinerConfigTest {

    @Test
    public void testConstruct() throws URISyntaxException {
        var env1 = Map.of(
            "MINER_ID", "miner01",
            "MINER_TYPE", "BITAXE",
            "MINER_URI", "http://127.0.0.1"
        );
        var env2 = Map.of(
            "MINER_ID", "miner02",
            "MINER_TYPE", "AVALON",
            "MINER_URI", "tcp://127.0.0.1:1235"
        );

        var cfg1 = new MinerConfig(env1);
        var cfg2 = new MinerConfig(env2);

        assertEquals("miner01", cfg1.getId());
        assertEquals("miner02", cfg2.getId());

        assertEquals(MinerType.BITAXE, cfg1.getType());
        assertEquals(MinerType.AVALON, cfg2.getType());

        assertEquals("http://127.0.0.1", cfg1.getUri().toString());
        assertEquals("tcp://127.0.0.1:1235", cfg2.getUri().toString());
    }
}
