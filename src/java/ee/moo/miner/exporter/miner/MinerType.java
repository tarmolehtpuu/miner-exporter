package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.antminer.Antminer;
import ee.moo.miner.exporter.miner.avalon.Avalon;
import ee.moo.miner.exporter.miner.bitaxe.Bitaxe;

public enum MinerType {
    ANTMINER {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Antminer(config, objectMapper);
        }
    },
    AVALON {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Avalon(config, objectMapper);
        }
    },
    BITAXE {
        @Override
        public Miner create(MinerConfig config, ObjectMapper objectMapper) {
            return new Bitaxe(config, objectMapper);
        }
    };

    public abstract Miner create(MinerConfig config, ObjectMapper objectMapper);
}
