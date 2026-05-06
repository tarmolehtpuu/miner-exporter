package ee.moo.miner.exporter.metrics;

import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerNotFoundException;
import ee.moo.miner.exporter.miner.MinerType;
import ee.moo.miner.exporter.util.StringUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MetricsController {

    private final List<Miner> miners;

    public String getMetrics(String id) {
        var miner = miners.stream()
            .filter(m -> StringUtil.equals(m.getId(), id))
            .findFirst()
            .orElse(null);

        if (miner == null) {
            throw new MinerNotFoundException("Miner not found (id=%s)", id);
        }

        return miner.getMetrics().export();
    }
}
