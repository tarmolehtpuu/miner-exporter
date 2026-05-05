package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.antminer.Antminer;
import ee.moo.miner.exporter.miner.bitaxe.Bitaxe;
import ee.moo.miner.exporter.miner.avalon.AvalonNano3;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class MinerFactory {

    private final ObjectMapper objectMapper;

    @SuppressWarnings({"EnhancedSwitchMigration"})
    public Miner create(MinerConfig config) {
        switch (config.getType()) {
            case ANTMINER:
                return createAntminer(config);
            case BITAXE:
                return createBitaxe(config);
            case NANO3:
                return createNano3(config);
            default:
                throw new MinerException("Unsupported miner type: %s", config.getType());
        }
    }

    private Miner createBitaxe(MinerConfig config) {
        return new Bitaxe(config, objectMapper);
    }

    private Miner createNano3(MinerConfig config) {
        return new AvalonNano3(config, objectMapper);
    }

    private Miner createAntminer(MinerConfig config) {
        return new Antminer(config, objectMapper);
    }
}
