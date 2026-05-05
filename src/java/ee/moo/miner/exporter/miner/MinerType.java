package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.antminer.Antminer;
import ee.moo.miner.exporter.miner.avalon.Avalon;
import ee.moo.miner.exporter.miner.bitaxe.Bitaxe;
import ee.moo.miner.exporter.miner.whatsminer.Whatsminer;

public enum MinerType {
    ANTMINER {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Antminer(config, objectMapper);
        }
    },
    BITAXE {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Bitaxe(config, objectMapper);
        }
    },
    AVALON {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Avalon(config, objectMapper);
        }
    },
    WHATSMINER {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Whatsminer(config, objectMapper);
        }
    };

    public abstract Miner create(MinerConfig config, ObjectMapper objectMapper);
}
