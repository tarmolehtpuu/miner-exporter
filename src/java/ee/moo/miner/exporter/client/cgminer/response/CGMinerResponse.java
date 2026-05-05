package ee.moo.miner.exporter.client.cgminer.response;

import ee.moo.miner.exporter.client.cgminer.model.CGMinerMeta;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Data
public class CGMinerResponse {

    private final CGMinerMeta meta;
    
    public final List<Map<String, Object>> items;

    public Map<String, Object> getItem() {
        return items.getFirst();
    }

}
