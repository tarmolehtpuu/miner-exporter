package ee.moo.miner.exporter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.moo.miner.exporter.api.DefaultController;
import ee.moo.miner.exporter.api.HealthzController;
import ee.moo.miner.exporter.api.MetricsController;
import ee.moo.miner.exporter.api.ReadyzController;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Slf4j
public class Application {

    private final Map<String, String> env;

    private final int port;

    private final Server server;

    public Application(Map<String, String> env, int port) {
        this.env = env;
        this.port = port;
        this.server = new Server();
    }

    public void start() throws Exception {
        var miners = createMiners(env);

        var connector = new ServerConnector(server);
        connector.setPort(port);

        server.addConnector(connector);
        server.setDefaultHandler(new DefaultController(List.of(
            new HealthzController(),
            new ReadyzController(),
            new MetricsController(miners)
        )));
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }

    public

    static void main() throws Exception {
        new Application(System.getenv(), 8080).start();
    }

    private static ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .defaultPropertyInclusion(JsonInclude.Value.construct(NON_ABSENT, NON_ABSENT))
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    private static List<Miner> createMiners(Map<String, String> env) {
        var mapper = createObjectMapper();
        var miners = new ArrayList<Miner>();

        for (var config : MinerConfig.createFromEnvironment(env)) {
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
