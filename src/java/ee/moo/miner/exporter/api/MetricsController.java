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
package ee.moo.miner.exporter.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ee.moo.miner.exporter.miner.Miner;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MetricsController implements HttpHandler {

    private static final Logger logger = Logger.getLogger(MetricsController.class.getName());

    private final Miner miner;

    public MetricsController(Miner miner) {
        this.miner = miner;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            var response = miner
                .getMetrics()
                .export();

            exchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length());

            try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes(UTF_8));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
