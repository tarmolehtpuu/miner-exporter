package ee.moo.miner.exporter.fake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Slf4j
public class FakeCGMiner implements Runnable {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, String> commands = new ConcurrentHashMap<>();

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final int port;

    private ServerSocket server;

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

        log.info("Started {} on port {}", getClass().getSimpleName(), port);
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

                    var response = getResponse(
                        objectMapper.readTree(buf)
                            .get("command")
                            .asText()
                    );

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
        try {
            var response = commands.containsKey(command)
                ? commands.get(command)
                : objectMapper.writeValueAsString(Map.of(
                "id", 1,
                "STATUS", List.of(
                    Map.of(
                        "STATUS", "E",
                        "When", 113585,
                        "Code", 14,
                        "Msg", "Invalid command"
                    ))));

            response = response.replace("\r", " ");
            response = response.replace("\n", " ");
            response = response.replaceAll("\\s+", " ");

            var bytes1 = response.getBytes(UTF_8);
            var bytes2 = new byte[bytes1.length + 1];

            System.arraycopy(bytes1, 0, bytes2, 0, bytes1.length);

            bytes2[bytes2.length - 1] = 0;

            return bytes2;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
