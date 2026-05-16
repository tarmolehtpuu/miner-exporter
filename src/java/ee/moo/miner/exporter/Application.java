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

import com.sun.net.httpserver.HttpServer;
import ee.moo.miner.exporter.api.HealthzController;
import ee.moo.miner.exporter.api.MetricsController;
import ee.moo.miner.exporter.api.ReadyzController;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Executors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Map<String, String> env;

    private final String host;

    private final int port;

    private HttpServer server;

    public Application(Map<String, String> env, String host, int port) {
        this.env = env;
        this.host = host;
        this.port = port;
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
            .create(config);
    }

    public void start() throws Exception {
        log.info("HttpServer starting...");

        server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        server.createContext("/healthz", new HealthzController());
        log.info("Context: /healthz -> HealthzController");

        server.createContext("/readyz", new ReadyzController());
        log.info("Context: /readyz  -> ReadyzController");

        var miner = miner(env);
        if (miner != null) {
            server.createContext("/metrics", new MetricsController(miner));
            log.info("Context: /metrics -> MetricsController");
            log.info(miner.toString());
        }

        server.start();

        log.info("Started HttpServer on: {}:{}", host, port);
    }

    public void stop() throws Exception {
        if (server != null) {
            log.info("HttpServer shutting down...");
            server.stop(5);
            log.info("HttpServer stopped");
        }
    }
}
