package ee.moo.miner.exporter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.moo.miner.exporter.api.*;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Slf4j
public class Application {

    private final Map<String, String> env;

    private final String host;

    private final int port;

    private final Server server;

    public Application(Map<String, String> env, String host, int port) {
        this.env = env;
        this.host = host;
        this.port = port;
        this.server = new Server();
    }

    public void start() throws Exception {
        var connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);

        var controllers = new ArrayList<Controller>();
        controllers.add(new HealthzController());
        controllers.add(new ReadyzController());

        var miner = miner(env);
        if (miner != null) {
            controllers.add(new MetricsController(miner));
        }

        server.addConnector(connector);
        server.setDefaultHandler(new DefaultController(controllers));
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }

    static void main() throws Exception {
        var env = System.getenv();
        var host = env.getOrDefault("LISTEN_HOST", "0.0.0.0");
        var port = env.containsKey("LISTEN_PORT")
            ? Integer.parseInt(env.get("LISTEN_PORT"))
            : 8080;

        new Application(env, host, port).start();
    }

    private static Miner miner(Map<String, String> env) throws URISyntaxException {
        if (!env.containsKey("MINER_ID") || !env.containsKey("MINER_TYPE") || !env.containsKey("MINER_URI")) {
            log.warn("No miners configured, skipping metrics");
            return null;
        }

        var config = new MinerConfig(env);
        return config
            .getType()
            .create(config, objectMapper());
    }

    private static ObjectMapper objectMapper() {
        return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .defaultPropertyInclusion(JsonInclude.Value.construct(NON_ABSENT, NON_ABSENT))
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }
}
