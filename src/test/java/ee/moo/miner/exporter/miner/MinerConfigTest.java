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
