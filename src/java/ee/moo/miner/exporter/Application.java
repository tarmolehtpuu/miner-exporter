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
import ee.moo.miner.exporter.util.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class Application {

    static {
        LogManager.getLogManager().reset();

        var root = Logger.getLogger("");
        root.addHandler(new StdoutHandler());
        root.setLevel(Level.INFO);
    }

    private static final Logger logger = Logger.getLogger(Application.class.getName());

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
            logger.warning("No miners configured, skipping metrics");
            return null;
        }

        var config = new MinerConfig(env);

        return config
            .getType()
            .create(config);
    }

    public void start() throws Exception {
        logger.info("HttpServer starting...");

        server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        server.createContext("/healthz", new HealthzController());
        logger.info("Context: /healthz -> HealthzController");

        server.createContext("/readyz", new ReadyzController());
        logger.info("Context: /readyz  -> ReadyzController");

        var miner = miner(env);
        if (miner != null) {
            server.createContext("/metrics", new MetricsController(miner));
            logger.info("Context: /metrics -> MetricsController");
            logger.info(miner.toString());
        }

        server.start();

        logger.log(Level.INFO, "Started HttpServer on: %s:%d", new Object[]{host, port});
    }

    public void stop() throws Exception {
        if (server != null) {
            logger.info("HttpServer shutting down...");
            server.stop(1);
            logger.info("HttpServer stopped");
        }
    }

    public static class StdoutHandler extends StreamHandler {

        public StdoutHandler() {
            setOutputStream(System.out);
            setFormatter(new StdoutFormatter());
            setLevel(Level.INFO);
        }

        @Override
        public void publish(LogRecord record) {
            super.publish(record);
            flush();
        }
    }

    public static class StdoutFormatter extends Formatter {

        private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            var sb = new StringBuilder(String.format(
                "[%s][%s][%s][%s]: %s%n",
                formatTime(record.getInstant()),
                Thread.currentThread().getName(),
                formatLevel(record.getLevel().getLocalizedName()),
                formatClass(record.getSourceClassName()),
                formatMessage(record)
            ));

            if (record.getThrown() != null) {
                try {
                    var sw = new StringWriter();
                    var pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    sb.append(sw);
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }

        private String formatTime(Instant instant) {
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(timeFormatter);
        }

        private String formatLevel(String level) {
            if (level.length() > 4) {
                level = level.substring(0, 4);
            }
            if (level.length() < 4) {
                level = StringUtil.rpad(level, ' ', 4);
            }

            return level;
        }

        private String formatClass(String name) {
            return name.substring(name.lastIndexOf('.') + 1);
        }

        @Override
        public String formatMessage(LogRecord record) {
            if (record.getParameters() == null || record.getParameters().length == 0) {
                return record.getMessage();
            }

            return String.format(record.getMessage(), record.getParameters());
        }
    }
}
