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
import ee.moo.miner.exporter.util.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ReadyzController implements HttpHandler {

    private static final Logger log = new Logger(ReadyzController.class);

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String response = "Ready!";

            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.length());

            try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes(UTF_8));
            }

            if (log.isEnabled(Logger.Level.DEBUG)) {
                log.debug("/readyz [200]");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
