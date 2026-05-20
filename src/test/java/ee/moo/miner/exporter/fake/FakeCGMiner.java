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
package ee.moo.miner.exporter.fake;

import ee.moo.tiny.common.log.Logger;
import ee.moo.tiny.json.Json;
import ee.moo.tiny.json.JsonArray;
import ee.moo.tiny.json.JsonObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FakeCGMiner implements Runnable {

    private static final Logger log = new Logger(FakeCGMiner.class);

    private final Map<String, String> commands = new ConcurrentHashMap<>();

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final int port;

    private ServerSocket server;

    public FakeCGMiner(int port) {
        this.port = port;
    }

    public void start() {
        if (running.get()) {
            return;
        }

        running.set(true);

        var thread = new Thread(this, String.format("%s-Thread", getClass().getSimpleName()));
        thread.start();

        int i = 0;
        while (server == null || !server.isBound()) {
            i++;
            if (i > 10) {
                throw new RuntimeException("Timeout waiting for server socket to start listening");
            }

            try {
                //noinspection BusyWait
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // ignored
            }
        }

        log.info("Started FakeCGMiner on port {}", port);
    }

    public void stop() {
        running.set(false);

        try {
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            // ignored
        }
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(500);

            while (running.get()) {
                try (var client = server.accept()) {
                    var buf = new byte[1024];
                    int len = client.getInputStream().read(buf);
                    if (len == 0) {
                        continue;
                    }

                    var bytes = new byte[len];
                    System.arraycopy(buf, 0, bytes, 0, len);

                    var response = getResponse(Json.readObject(bytes).get("command").asString());

                    client.getOutputStream().write(response);
                    client.getOutputStream().flush();

                } catch (SocketTimeoutException e) {
                    // probably server.accept() timeout
                } catch (Exception e) {
                    if (running.get()) {
                        log.error("Client error: {}", e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            stop();
        }
    }

    public void stub(String command, String response) {
        commands.put(command, response);
    }

    public void resetAll() {
        commands.clear();
    }

    private byte[] getResponse(String command) {
        var response = commands.getOrDefault(command, getErrorResponse());

        response = response.replace("\r", " ");
        response = response.replace("\n", " ");
        response = response.replaceAll("\\s+", " ");

        var bytes1 = response.getBytes(UTF_8);
        var bytes2 = new byte[bytes1.length + 1];

        System.arraycopy(bytes1, 0, bytes2, 0, bytes1.length);

        bytes2[bytes2.length - 1] = 0;

        return bytes2;
    }

    private String getErrorResponse() {
        var body = new JsonObject();
        body.put("STATUS", "E");
        body.put("When", 113585);
        body.put("Code", 14);
        body.put("Msg", "Invalid command");

        var json = new JsonObject();
        json.put("id", 1);
        json.put("STATUS", new JsonArray(List.of(body)));

        return Json.write(json);
    }
}
