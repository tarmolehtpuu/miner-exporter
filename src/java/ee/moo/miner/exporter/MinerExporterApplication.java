package ee.moo.miner.exporter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.moo.miner.exporter.handler.DefaultHandler;
import ee.moo.miner.exporter.metrics.MetricsController;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.*;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Slf4j
public class MinerExporterApplication {

    static void main() throws Exception {
        var miners = createMiners();
        var server = new Server();

        var connector = new ServerConnector(server);
        connector.setPort(8080);

        server.addConnector(connector);
        server.setDefaultHandler(new DefaultHandler(new MetricsController(miners)));
        server.start();
        server.join();
    }

    private static ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .defaultPropertyInclusion(
                JsonInclude.Value.construct(NON_ABSENT, NON_ABSENT)
            )
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    private static List<Miner> createMiners() {
        var mapper = createObjectMapper();
        var miners = new ArrayList<Miner>();

        for (var config : MinerConfig.createFromEnvironment()) {
            var miner = config.getType().create(config, mapper);

            log.info(
                "Adding miner: id='{}', type={}, uri='{}'",
                miner.getId(),
                miner.getType(),
                miner.getUri()
            );

            miners.add(miner);
        }

        if (miners.isEmpty()) {
            log.warn("No miners configured");
        }

        return miners;
    }
}
