package ee.moo.miner.exporter.metrics;

import ee.moo.miner.exporter.miner.Miner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MetricsController {

    private final List<Miner> miners;

    @GetMapping("/metrics/{id}")
    public ResponseEntity<String> getMetrics(@PathVariable String id) {
        var miner = miners.stream()
            .filter(m -> m.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (miner == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body(String.format("Miner not configured: %s", id));
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
            .body(miner.getMetrics().export());
    }
}
