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
package ee.moo.miner.exporter;

import ee.moo.miner.exporter.api.*;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

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

    static void main() throws Exception {
        var env = System.getenv();
        var host = env.getOrDefault("LISTEN_HOST", "0.0.0.0");
        var port = env.containsKey("LISTEN_PORT")
            ? Integer.parseInt(env.get("LISTEN_PORT"))
            : 8080;

        new Application(env, host, port).start();
    }

    private static Optional<Miner> miner(Map<String, String> env) throws URISyntaxException {
        if (!env.containsKey("MINER_ID") || !env.containsKey("MINER_TYPE") || !env.containsKey("MINER_URI")) {
            log.warn("No miners configured, skipping metrics");
            return Optional.empty();
        }

        var config = new MinerConfig(env);
        return Optional.of(config
            .getType()
            .create(config)
        );
    }

    public void start() throws Exception {
        var connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);

        var controllers = new ArrayList<Controller>();
        controllers.add(new HealthzController());
        controllers.add(new ReadyzController());

        var miner = miner(env);
        miner.ifPresent(m -> controllers.add(new MetricsController(m)));

        server.addConnector(connector);
        server.setDefaultHandler(new DefaultController(controllers));
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }
}
